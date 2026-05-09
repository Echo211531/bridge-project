package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.dto.MaintainRecordDTO;
import com.bridge.lifecycle.entity.*;
import com.bridge.lifecycle.mapper.*;
import com.bridge.lifecycle.vo.MaintainPlanVO;
import com.bridge.lifecycle.vo.MaintainRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保养管理服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaintainService {

    private final MaintainPlanMapper maintainPlanMapper;
    private final MaintainRecordMapper maintainRecordMapper;
    private final DeviceArchiveMapper deviceArchiveMapper;
    private final DeviceCategoryMapper deviceCategoryMapper;
    private final BridgeMapper bridgeMapper;
    private final LifecycleEventMapper lifecycleEventMapper;

    // 状态名称映射
    private static final java.util.Map<String, String> STATUS_NAME_MAP = java.util.Map.of(
            "pending", "待保养",
            "completed", "已完成",
            "overdue", "超期"
    );

    /**
     * 计算动态保养周期
     * 公式：标准周期 × α × β
     *
     * @param device 设备档案
     * @return 动态周期天数
     */
    public Integer calculateDynamicCycle(DeviceArchive device) {
        // 获取设备分类
        DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
        if (category == null || category.getMaintainCycleDays() == null) {
            return 90;  // 默认90天
        }

        // 获取桥梁信息
        Bridge bridge = bridgeMapper.selectById(device.getBridgeId());
        if (bridge == null) {
            return category.getMaintainCycleDays();
        }

        // 计算动态周期：标准周期 × α × β
        int standardCycle = category.getMaintainCycleDays();
        double alpha = bridge.getAlphaCoef().doubleValue();
        double beta = bridge.getBetaCoef().doubleValue();

        int dynamicCycle = (int) Math.ceil(standardCycle * alpha * beta);
        return dynamicCycle;
    }

    /**
     * 保养计划列表查询
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param status   状态筛选
     * @return 分页保养计划列表
     */
    public Page<MaintainPlanVO> listPlans(Integer pageNum, Integer pageSize, String status) {
        Page<MaintainPlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MaintainPlan> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MaintainPlan::getStatus, status);
        }
        // 按计划日期升序（优先显示即将到期的）
        wrapper.orderByAsc(MaintainPlan::getPlanDate);

        Page<MaintainPlan> planPage = maintainPlanMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<MaintainPlanVO> voPage = new Page<>(planPage.getCurrent(), planPage.getSize(), planPage.getTotal());
        List<MaintainPlanVO> voList = planPage.getRecords().stream()
                .map(this::convertPlanToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 保养记录列表查询
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param deviceId 设备ID筛选
     * @return 分页保养记录列表
     */
    public Page<MaintainRecordVO> listRecords(Integer pageNum, Integer pageSize, String deviceId) {
        Page<MaintainRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MaintainRecord> wrapper = new LambdaQueryWrapper<>();

        // 设备筛选
        if (deviceId != null && !deviceId.isEmpty()) {
            wrapper.eq(MaintainRecord::getDeviceId, deviceId);
        }
        // 按保养日期降序
        wrapper.orderByDesc(MaintainRecord::getRecordDate);

        Page<MaintainRecord> recordPage = maintainRecordMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<MaintainRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        List<MaintainRecordVO> voList = recordPage.getRecords().stream()
                .map(this::convertRecordToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 保养记录上报（生成生命周期事件）
     *
     * @param recordDTO 保养记录请求
     * @return 保养记录
     */
    @Transactional(rollbackFor = Exception.class)
    public MaintainRecordVO submitRecord(MaintainRecordDTO recordDTO) {
        // 检查设备是否存在
        DeviceArchive device = deviceArchiveMapper.selectById(recordDTO.getDeviceId());
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        // 创建保养记录
        MaintainRecord record = new MaintainRecord();
        record.setDeviceId(recordDTO.getDeviceId());
        record.setRecordDate(recordDTO.getRecordDate());
        record.setActualCost(recordDTO.getActualCost());
        record.setManhour(recordDTO.getManhour());
        record.setOperator(recordDTO.getOperator());
        record.setContent(recordDTO.getContent());
        maintainRecordMapper.insert(record);

        // 更新保养计划状态
        MaintainPlan plan = maintainPlanMapper.selectOne(
                new LambdaQueryWrapper<MaintainPlan>()
                        .eq(MaintainPlan::getDeviceId, recordDTO.getDeviceId())
                        .eq(MaintainPlan::getStatus, "pending")
                        .orderByAsc(MaintainPlan::getPlanDate)
                        .last("LIMIT 1")
        );
        if (plan != null) {
            plan.setStatus("completed");
            maintainPlanMapper.updateById(plan);

            // 自动生成下次计划
            generateNextPlan(device, plan.getPlanDate());
        }

        // 生成生命周期事件
        LifecycleEvent event = new LifecycleEvent();
        event.setDeviceId(device.getId());
        event.setEventType("maintain");
        event.setTitle("保养完成");
        event.setDescription(String.format("设备 %s 保养完成，费用 %.2f 元，内容：%s",
                device.getDeviceName(), recordDTO.getActualCost(), recordDTO.getContent()));
        event.setCost(recordDTO.getActualCost());
        event.setOperator(recordDTO.getOperator());
        event.setEventTime(LocalDateTime.now());
        lifecycleEventMapper.insert(event);

        // 更新设备状态
        if ("maintaining".equals(device.getStatus())) {
            device.setStatus("in_use");
            deviceArchiveMapper.updateById(device);
        }

        log.info("保养记录上报成功: 设备={}, 日期={}", device.getDeviceCode(), recordDTO.getRecordDate());

        return convertRecordToVO(record);
    }

    /**
     * 自动生成下次保养计划
     *
     * @param device    设备档案
     * @param lastDate  上次计划日期
     */
    private void generateNextPlan(DeviceArchive device, LocalDate lastDate) {
        Integer dynamicCycle = calculateDynamicCycle(device);
        LocalDate nextDate = lastDate.plusDays(dynamicCycle);

        MaintainPlan nextPlan = new MaintainPlan();
        nextPlan.setDeviceId(device.getId());
        nextPlan.setPlanDate(nextDate);
        nextPlan.setCycleDays(dynamicCycle);
        nextPlan.setStatus("pending");
        maintainPlanMapper.insert(nextPlan);

        log.info("自动生成下次保养计划: 设备={}, 下次日期={}", device.getDeviceCode(), nextDate);
    }

    /**
     * 保养完成率统计
     *
     * @param year 年份
     * @return 完成率
     */
    public BigDecimal calculateCompletionRate(int year) {
        // 统计本年计划总数
        Long totalPlans = maintainPlanMapper.selectCount(
                new LambdaQueryWrapper<MaintainPlan>()
                        .ge(MaintainPlan::getPlanDate, LocalDate.of(year, 1, 1))
                        .le(MaintainPlan::getPlanDate, LocalDate.of(year, 12, 31))
        );

        // 统计本年已完成数
        Long completedPlans = maintainPlanMapper.selectCount(
                new LambdaQueryWrapper<MaintainPlan>()
                        .ge(MaintainPlan::getPlanDate, LocalDate.of(year, 1, 1))
                        .le(MaintainPlan::getPlanDate, LocalDate.of(year, 12, 31))
                        .eq(MaintainPlan::getStatus, "completed")
        );

        if (totalPlans == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(completedPlans)
                .divide(BigDecimal.valueOf(totalPlans), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 转换计划实体为VO
     */
    private MaintainPlanVO convertPlanToVO(MaintainPlan plan) {
        MaintainPlanVO vo = new MaintainPlanVO();
        vo.setId(plan.getId());
        vo.setDeviceId(plan.getDeviceId());
        vo.setPlanDate(plan.getPlanDate());
        vo.setCycleDays(plan.getCycleDays());
        vo.setStatus(plan.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.getOrDefault(plan.getStatus(), ""));
        vo.setCreateTime(plan.getCreateTime());

        // 获取设备信息
        DeviceArchive device = deviceArchiveMapper.selectById(plan.getDeviceId());
        if (device != null) {
            vo.setDeviceCode(device.getDeviceCode());
            vo.setDeviceName(device.getDeviceName());

            // 获取桥梁信息
            Bridge bridge = bridgeMapper.selectById(device.getBridgeId());
            if (bridge != null) {
                vo.setBridgeName(bridge.getBridgeName());

                // 计算动态调整系数
                BigDecimal alpha = bridge.getAlphaCoef();
                BigDecimal beta = bridge.getBetaCoef();
                vo.setDynamicFactor(String.format("α=%.2f × β=%.2f = %.2f",
                        alpha, beta, alpha.multiply(beta)));
            }

            // 获取标准周期
            DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
            if (category != null) {
                vo.setStandardCycleDays(category.getMaintainCycleDays());
            }
        }

        return vo;
    }

    /**
     * 转换记录实体为VO
     */
    private MaintainRecordVO convertRecordToVO(MaintainRecord record) {
        MaintainRecordVO vo = new MaintainRecordVO();
        vo.setId(record.getId());
        vo.setDeviceId(record.getDeviceId());
        vo.setRecordDate(record.getRecordDate());
        vo.setActualCost(record.getActualCost());
        vo.setManhour(record.getManhour());
        vo.setOperator(record.getOperator());
        vo.setContent(record.getContent());
        vo.setCreateTime(record.getCreateTime());

        // 获取设备信息
        DeviceArchive device = deviceArchiveMapper.selectById(record.getDeviceId());
        if (device != null) {
            vo.setDeviceCode(device.getDeviceCode());
            vo.setDeviceName(device.getDeviceName());

            // 获取桥梁信息
            Bridge bridge = bridgeMapper.selectById(device.getBridgeId());
            if (bridge != null) {
                vo.setBridgeName(bridge.getBridgeName());
            }
        }

        return vo;
    }
}
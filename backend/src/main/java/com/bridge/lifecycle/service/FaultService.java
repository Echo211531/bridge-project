package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.dto.FaultCloseDTO;
import com.bridge.lifecycle.dto.FaultOrderDTO;
import com.bridge.lifecycle.entity.DeviceArchive;
import com.bridge.lifecycle.entity.FaultOrder;
import com.bridge.lifecycle.entity.LifecycleEvent;
import com.bridge.lifecycle.mapper.DeviceArchiveMapper;
import com.bridge.lifecycle.mapper.FaultOrderMapper;
import com.bridge.lifecycle.mapper.LifecycleEventMapper;
import com.bridge.lifecycle.vo.FaultOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 故障工单管理服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FaultService {

    private final FaultOrderMapper faultOrderMapper;
    private final DeviceArchiveMapper deviceArchiveMapper;
    private final LifecycleEventMapper lifecycleEventMapper;

    // 状态名称映射
    private static final java.util.Map<String, String> STATUS_NAME_MAP = java.util.Map.of(
            "open", "待处理",
            "processing", "处理中",
            "closed", "已关闭"
    );

    /**
     * 生成工单编码
     * 规则：FLT-年月-序号
     *
     * @return 工单编码
     */
    public String generateOrderNo() {
        LocalDate now = LocalDate.now();
        String prefix = String.format("FLT-%d%02d-", now.getYear(), now.getMonthValue());

        // 查询本月工单数量
        Long count = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .likeRight(FaultOrder::getOrderNo, prefix)
        );

        return String.format("%s%03d", prefix, count + 1);
    }

    /**
     * 计算MTTR（平均修复时间）
     *
     * @param year 年份
     * @return MTTR(小时)
     */
    public BigDecimal calculateMTTR(int year) {
        List<FaultOrder> closedOrders = faultOrderMapper.selectList(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getStatus, "closed")
                        .ge(FaultOrder::getReportDate, LocalDate.of(year, 1, 1))
                        .le(FaultOrder::getReportDate, LocalDate.of(year, 12, 31))
                        .isNotNull(FaultOrder::getCloseDate)
        );

        if (closedOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 计算平均修复时间（天）
        long totalDays = closedOrders.stream()
                .mapToLong(o -> ChronoUnit.DAYS.between(o.getReportDate(), o.getCloseDate()))
                .sum();

        // 转换为小时
        BigDecimal mttr = BigDecimal.valueOf(totalDays * 24)
                .divide(BigDecimal.valueOf(closedOrders.size()), 2, RoundingMode.HALF_UP);

        return mttr;
    }

    /**
     * 计算MTBF（平均故障间隔时间）
     *
     * @param deviceId 设备ID
     * @return MTBF(天)
     */
    public BigDecimal calculateMTBF(String deviceId) {
        List<FaultOrder> orders = faultOrderMapper.selectList(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getDeviceId, deviceId)
                        .eq(FaultOrder::getStatus, "closed")
                        .orderByAsc(FaultOrder::getReportDate)
        );

        if (orders.size() < 2) {
            return BigDecimal.ZERO;
        }

        // 计算故障间隔天数总和
        long totalDays = 0;
        for (int i = 1; i < orders.size(); i++) {
            totalDays += ChronoUnit.DAYS.between(orders.get(i - 1).getCloseDate(), orders.get(i).getReportDate());
        }

        // 平均间隔天数
        BigDecimal mtbf = BigDecimal.valueOf(totalDays)
                .divide(BigDecimal.valueOf(orders.size() - 1), 2, RoundingMode.HALF_UP);

        return mtbf;
    }

    /**
     * 计算年故障率
     *
     * @param year 年份
     * @return 故障率(百分比)
     */
    public BigDecimal calculateFaultRate(int year) {
        // 统计本年故障工单数
        Long faultCount = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .ge(FaultOrder::getReportDate, LocalDate.of(year, 1, 1))
                        .le(FaultOrder::getReportDate, LocalDate.of(year, 12, 31))
        );

        // 统计设备总数
        Long deviceCount = deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
        );

        if (deviceCount == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(faultCount)
                .divide(BigDecimal.valueOf(deviceCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算工单关闭率
     *
     * @param year 年份
     * @return 关闭率(百分比)
     */
    public BigDecimal calculateCloseRate(int year) {
        // 统计本年工单总数
        Long totalCount = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .ge(FaultOrder::getReportDate, LocalDate.of(year, 1, 1))
                        .le(FaultOrder::getReportDate, LocalDate.of(year, 12, 31))
        );

        // 统计已关闭工单数
        Long closedCount = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .ge(FaultOrder::getReportDate, LocalDate.of(year, 1, 1))
                        .le(FaultOrder::getReportDate, LocalDate.of(year, 12, 31))
                        .eq(FaultOrder::getStatus, "closed")
        );

        if (totalCount == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(closedCount)
                .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 工单列表分页查询
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param status   状态筛选
     * @return 分页工单列表
     */
    public Page<FaultOrderVO> listOrders(Integer pageNum, Integer pageSize, String status) {
        Page<FaultOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FaultOrder> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (status != null && !status.isEmpty()) {
            wrapper.eq(FaultOrder::getStatus, status);
        }
        // 按申报日期降序
        wrapper.orderByDesc(FaultOrder::getReportDate);

        Page<FaultOrder> orderPage = faultOrderMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<FaultOrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<FaultOrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 故障申报（设备状态变更、生成事件）
     *
     * @param orderDTO 故障申报请求
     * @return 工单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public FaultOrderVO reportFault(FaultOrderDTO orderDTO) {
        // 检查设备是否存在
        DeviceArchive device = deviceArchiveMapper.selectById(orderDTO.getDeviceId());
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        // 创建工单
        FaultOrder order = new FaultOrder();
        order.setOrderNo(generateOrderNo());
        order.setDeviceId(orderDTO.getDeviceId());
        order.setReportDate(orderDTO.getReportDate() != null ? orderDTO.getReportDate() : LocalDate.now());
        order.setFaultDesc(orderDTO.getFaultDesc());
        order.setStatus("open");
        faultOrderMapper.insert(order);

        // 更新设备状态为故障
        device.setStatus("fault");
        deviceArchiveMapper.updateById(device);

        // 生成生命周期事件
        LifecycleEvent event = new LifecycleEvent();
        event.setDeviceId(device.getId());
        event.setEventType("repair");
        event.setTitle("故障申报");
        event.setDescription(String.format("设备 %s 发生故障：%s", device.getDeviceName(), orderDTO.getFaultDesc()));
        event.setEventTime(LocalDateTime.now());
        lifecycleEventMapper.insert(event);

        log.info("故障申报成功: 工单号={}, 设备={}", order.getOrderNo(), device.getDeviceCode());

        return convertToVO(order);
    }

    /**
     * 工单处理（状态流转）
     *
     * @param id     工单ID
     * @param status 新状态
     * @return 工单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public FaultOrderVO processOrder(String id, String status) {
        FaultOrder order = faultOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }

        order.setStatus(status);
        faultOrderMapper.updateById(order);

        log.info("工单状态变更: {} -> {}", order.getOrderNo(), status);

        return convertToVO(order);
    }

    /**
     * 工单关闭（设备状态恢复、MTTR计算）
     *
     * @param closeDTO 工单关闭请求
     * @return 工单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public FaultOrderVO closeOrder(FaultCloseDTO closeDTO) {
        FaultOrder order = faultOrderMapper.selectById(closeDTO.getOrderId());
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }

        // 更新工单信息
        order.setRepairCost(closeDTO.getRepairCost());
        order.setRepairContent(closeDTO.getRepairContent());
        order.setCloseDate(closeDTO.getCloseDate() != null ? closeDTO.getCloseDate() : LocalDate.now());
        order.setHandler(closeDTO.getHandler());
        order.setStatus("closed");
        faultOrderMapper.updateById(order);

        // 恢复设备状态
        DeviceArchive device = deviceArchiveMapper.selectById(order.getDeviceId());
        if (device != null) {
            device.setStatus("in_use");
            deviceArchiveMapper.updateById(device);
        }

        // 生成生命周期事件
        if (device != null) {
            LifecycleEvent event = new LifecycleEvent();
            event.setDeviceId(device.getId());
            event.setEventType("repair");
            event.setTitle("故障修复");
            event.setDescription(String.format("设备 %s 故障修复完成，费用 %.2f 元",
                    device.getDeviceName(), closeDTO.getRepairCost()));
            event.setCost(closeDTO.getRepairCost());
            event.setOperator(closeDTO.getHandler());
            event.setEventTime(LocalDateTime.now());
            lifecycleEventMapper.insert(event);
        }

        log.info("工单关闭成功: 工单号={}, 修复费用={}", order.getOrderNo(), closeDTO.getRepairCost());

        return convertToVO(order);
    }

    /**
     * 转换实体为VO
     */
    private FaultOrderVO convertToVO(FaultOrder order) {
        FaultOrderVO vo = new FaultOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setDeviceId(order.getDeviceId());
        vo.setReportDate(order.getReportDate());
        vo.setFaultDesc(order.getFaultDesc());
        vo.setRepairCost(order.getRepairCost());
        vo.setRepairContent(order.getRepairContent());
        vo.setStatus(order.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.getOrDefault(order.getStatus(), ""));
        vo.setCloseDate(order.getCloseDate());
        vo.setHandler(order.getHandler());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        // 计算处理时长
        if (order.getCloseDate() != null) {
            vo.setDurationDays(ChronoUnit.DAYS.between(order.getReportDate(), order.getCloseDate()));
        } else {
            vo.setDurationDays(ChronoUnit.DAYS.between(order.getReportDate(), LocalDate.now()));
        }

        // 获取设备信息
        DeviceArchive device = deviceArchiveMapper.selectById(order.getDeviceId());
        if (device != null) {
            vo.setDeviceCode(device.getDeviceCode());
            vo.setDeviceName(device.getDeviceName());
        }

        return vo;
    }
}
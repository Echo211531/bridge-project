package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bridge.lifecycle.entity.*;
import com.bridge.lifecycle.mapper.*;
import com.bridge.lifecycle.vo.LifecycleEventVO;
import com.bridge.lifecycle.vo.LifecycleSummaryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生命周期管理服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LifecycleService {

    private final LifecycleEventMapper lifecycleEventMapper;
    private final DeviceArchiveMapper deviceArchiveMapper;
    private final DeviceCategoryMapper deviceCategoryMapper;
    private final BridgeMapper bridgeMapper;
    private final MaintainRecordMapper maintainRecordMapper;
    private final FaultOrderMapper faultOrderMapper;

    // 事件类型名称映射
    private static final java.util.Map<String, String> EVENT_TYPE_NAME_MAP = java.util.Map.of(
            "purchase", "采购入库",
            "commission", "投入使用",
            "maintain", "保养维护",
            "repair", "故障维修",
            "inspect", "技术鉴定",
            "scrap", "报废处理"
    );

    // 设备状态名称映射
    private static final java.util.Map<String, String> STATUS_NAME_MAP = java.util.Map.of(
            "in_use", "在用",
            "in_stock", "库存",
            "maintaining", "保养中",
            "fault", "故障",
            "scrapped", "已报废",
            "disabled", "禁用"
    );

    /**
     * 设备生命周期事件查询
     *
     * @param deviceId 设备ID
     * @return 生命周期事件列表
     */
    public List<LifecycleEventVO> getEvents(String deviceId) {
        List<LifecycleEvent> events = lifecycleEventMapper.selectList(
                new LambdaQueryWrapper<LifecycleEvent>()
                        .eq(LifecycleEvent::getDeviceId, deviceId)
                        .orderByDesc(LifecycleEvent::getEventTime)
        );
        return events.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 生命周期汇总查询（累计TCO）
     *
     * @param deviceId 设备ID
     * @return 生命周期汇总
     */
    public LifecycleSummaryVO getSummary(String deviceId) {
        DeviceArchive device = deviceArchiveMapper.selectById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        LifecycleSummaryVO vo = new LifecycleSummaryVO();
        vo.setDeviceId(deviceId);
        vo.setDeviceCode(device.getDeviceCode());
        vo.setDeviceName(device.getDeviceName());
        vo.setPurchaseCost(device.getPurchaseCost());
        vo.setCurrentStatus(device.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.getOrDefault(device.getStatus(), ""));

        // 获取分类信息
        DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        // 获取桥梁信息
        Bridge bridge = bridgeMapper.selectById(device.getBridgeId());
        if (bridge != null) {
            vo.setBridgeName(bridge.getBridgeName());
        }

        // 统计保养费用
        BigDecimal totalMaintainCost = maintainRecordMapper.selectList(
                new LambdaQueryWrapper<MaintainRecord>()
                        .eq(MaintainRecord::getDeviceId, deviceId)
        ).stream()
                .map(MaintainRecord::getActualCost)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalMaintainCost(totalMaintainCost);

        // 统计保养次数
        Long maintainCount = maintainRecordMapper.selectCount(
                new LambdaQueryWrapper<MaintainRecord>()
                        .eq(MaintainRecord::getDeviceId, deviceId)
        );
        vo.setMaintainCount(maintainCount.intValue());

        // 统计维修费用
        BigDecimal totalRepairCost = faultOrderMapper.selectList(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getDeviceId, deviceId)
                        .eq(FaultOrder::getStatus, "closed")
        ).stream()
                .map(FaultOrder::getRepairCost)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalRepairCost(totalRepairCost);

        // 统计维修次数
        Long repairCount = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getDeviceId, deviceId)
        );
        vo.setRepairCount(repairCount.intValue());

        // 计算累计TCO
        BigDecimal totalTco = device.getPurchaseCost()
                .add(totalMaintainCost)
                .add(totalRepairCost);
        vo.setTotalTco(totalTco);

        // 计算当前残值
        if (category != null) {
            vo.setCurrentResidualValue(calculateResidualValue(device, category));
        }

        // TCO占比分析
        if (totalTco.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal purchaseRate = device.getPurchaseCost()
                    .divide(totalTco, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            BigDecimal maintainRate = totalMaintainCost
                    .divide(totalTco, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            BigDecimal repairRate = totalRepairCost
                    .divide(totalTco, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            vo.setTcoAnalysis(String.format("购置占比%.1f%%，保养占比%.1f%%，维修占比%.1f%%",
                    purchaseRate, maintainRate, repairRate));
        }

        return vo;
    }

    /**
     * 自动生成生命周期事件
     *
     * @param deviceId   设备ID
     * @param eventType  事件类型
     * @param title      事件标题
     * @param description 事件描述
     * @param cost       事件成本
     * @param operator   操作人
     */
    public void createEvent(String deviceId, String eventType, String title,
                            String description, BigDecimal cost, String operator) {
        LifecycleEvent event = new LifecycleEvent();
        event.setDeviceId(deviceId);
        event.setEventType(eventType);
        event.setTitle(title);
        event.setDescription(description);
        event.setCost(cost);
        event.setOperator(operator);
        event.setEventTime(LocalDateTime.now());
        lifecycleEventMapper.insert(event);

        log.info("生命周期事件生成: 设备={}, 类型={}, 标题={}", deviceId, eventType, title);
    }

    /**
     * 生命周期成本追溯
     *
     * @param deviceId 设备ID
     * @return 成本追溯详情
     */
    public String getCostTrace(String deviceId) {
        LifecycleSummaryVO summary = getSummary(deviceId);
        return String.format(
                "设备 %s 生命周期成本追溯:\n" +
                "- 购置成本: %.2f 元\n" +
                "- 累计保养费用: %.2f 元 (%d 次)\n" +
                "- 累计维修费用: %.2f 元 (%d 次)\n" +
                "- 累计TCO: %.2f 元\n" +
                "- 当前残值: %.2f 元\n" +
                "- %s",
                summary.getDeviceName(),
                summary.getPurchaseCost(),
                summary.getTotalMaintainCost(), summary.getMaintainCount(),
                summary.getTotalRepairCost(), summary.getRepairCount(),
                summary.getTotalTco(),
                summary.getCurrentResidualValue(),
                summary.getTcoAnalysis()
        );
    }

    /**
     * 计算残值
     */
    private BigDecimal calculateResidualValue(DeviceArchive device, DeviceCategory category) {
        if (device.getPurchaseCost() == null || category.getDesignLifeYears() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal usedYears = BigDecimal.ZERO;
        if (device.getInUseDate() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(device.getInUseDate(), java.time.LocalDate.now());
            usedYears = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        }

        BigDecimal designLife = BigDecimal.valueOf(category.getDesignLifeYears());
        BigDecimal depreciationRate = usedYears.divide(designLife, 4, RoundingMode.HALF_UP);
        if (depreciationRate.compareTo(BigDecimal.ONE) > 0) {
            depreciationRate = BigDecimal.ONE;
        }

        BigDecimal depreciatedValue = device.getPurchaseCost().multiply(BigDecimal.ONE.subtract(depreciationRate));
        BigDecimal marketDiscount = new BigDecimal("0.70");

        return depreciatedValue.multiply(marketDiscount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 转换实体为VO
     */
    private LifecycleEventVO convertToVO(LifecycleEvent event) {
        LifecycleEventVO vo = new LifecycleEventVO();
        vo.setId(event.getId());
        vo.setDeviceId(event.getDeviceId());
        vo.setEventType(event.getEventType());
        vo.setEventTypeName(EVENT_TYPE_NAME_MAP.getOrDefault(event.getEventType(), ""));
        vo.setTitle(event.getTitle());
        vo.setDescription(event.getDescription());
        vo.setCost(event.getCost());
        vo.setOperator(event.getOperator());
        vo.setEventTime(event.getEventTime());
        vo.setCreateTime(event.getCreateTime());

        // 获取设备信息
        DeviceArchive device = deviceArchiveMapper.selectById(event.getDeviceId());
        if (device != null) {
            vo.setDeviceCode(device.getDeviceCode());
            vo.setDeviceName(device.getDeviceName());
        }

        return vo;
    }
}
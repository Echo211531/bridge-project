package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.dto.ScrapDecisionDTO;
import com.bridge.lifecycle.entity.*;
import com.bridge.lifecycle.mapper.*;
import com.bridge.lifecycle.vo.ScrapCandidateVO;
import com.bridge.lifecycle.vo.TcoDecisionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 报废鉴定服务类（TCO决策算法核心）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapService {

    private final DeviceArchiveMapper deviceArchiveMapper;
    private final DeviceCategoryMapper deviceCategoryMapper;
    private final BridgeMapper bridgeMapper;
    private final FaultOrderMapper faultOrderMapper;
    private final MaintainRecordMapper maintainRecordMapper;
    private final ScrapDecisionMapper scrapDecisionMapper;
    private final ScrapApprovalMapper scrapApprovalMapper;
    private final LifecycleEventMapper lifecycleEventMapper;
    private final ConfigMapper configMapper;

    /**
     * 待鉴定设备筛选（寿命警戒线、故障次数）
     *
     * @return 待鉴定设备列表
     */
    public Page<ScrapCandidateVO> listCandidates(int pageNum, int pageSize) {
        // 查询所有在用设备
        List<DeviceArchive> devices = deviceArchiveMapper.selectList(
                new LambdaQueryWrapper<DeviceArchive>()
                        .eq(DeviceArchive::getStatus, "in_use")
        );
        if (devices.isEmpty()) {
            return new Page<>(pageNum, pageSize, 0);
        }

        List<String> deviceIds = devices.stream()
                .map(DeviceArchive::getId)
                .collect(Collectors.toList());
        Map<String, DeviceCategory> categoryMap = deviceCategoryMapper.selectList(new LambdaQueryWrapper<DeviceCategory>())
                .stream()
                .collect(Collectors.toMap(DeviceCategory::getCode, category -> category));
        Map<String, Bridge> bridgeMap = bridgeMapper.selectList(new LambdaQueryWrapper<Bridge>())
                .stream()
                .collect(Collectors.toMap(Bridge::getId, bridge -> bridge));
        List<FaultOrder> faultOrders = faultOrderMapper.selectList(
                new LambdaQueryWrapper<FaultOrder>()
                        .in(FaultOrder::getDeviceId, deviceIds)
        );
        List<MaintainRecord> maintainRecords = maintainRecordMapper.selectList(
                new LambdaQueryWrapper<MaintainRecord>()
                        .in(MaintainRecord::getDeviceId, deviceIds)
        );
        Map<String, Long> faultCountMap = faultOrders.stream()
                .collect(Collectors.groupingBy(FaultOrder::getDeviceId, Collectors.counting()));
        Map<String, BigDecimal> repairCostMap = faultOrders.stream()
                .filter(order -> "closed".equals(order.getStatus()) && order.getRepairCost() != null)
                .collect(Collectors.groupingBy(
                        FaultOrder::getDeviceId,
                        Collectors.reducing(BigDecimal.ZERO, FaultOrder::getRepairCost, BigDecimal::add)
                ));
        Map<String, BigDecimal> maintainCostMap = maintainRecords.stream()
                .filter(record -> record.getActualCost() != null)
                .collect(Collectors.groupingBy(
                        MaintainRecord::getDeviceId,
                        Collectors.reducing(BigDecimal.ZERO, MaintainRecord::getActualCost, BigDecimal::add)
                ));

        // 获取阈值配置
        BigDecimal lifeThreshold = getConfigValue("life_threshold", new BigDecimal("80")); // 寿命警戒线(80%)
        BigDecimal residualThreshold = getConfigValue("residual_threshold", new BigDecimal("20")); // 残值警戒线(20%)
        Integer faultThreshold = getConfigIntValue("fault_threshold", 3); // 故障次数警戒线

        // 筛选候选设备
        List<ScrapCandidateVO> candidates = devices.stream()
                .map(device -> convertToCandidateVO(device, categoryMap, bridgeMap, faultCountMap, repairCostMap, maintainCostMap))
                .filter(vo -> isCandidate(vo, lifeThreshold, residualThreshold, faultThreshold))
                .sorted(Comparator.comparing(ScrapCandidateVO::getResidualRate, Comparator.nullsLast(BigDecimal::compareTo)))
                .collect(Collectors.toList());
        long total = candidates.size();

        int fromIndex = Math.max((pageNum - 1) * pageSize, 0);
        int toIndex = Math.min(fromIndex + pageSize, candidates.size());
        List<ScrapCandidateVO> pageRecords = fromIndex >= toIndex
                ? Collections.emptyList()
                : candidates.subList(fromIndex, toIndex);

        Page<ScrapCandidateVO> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(pageRecords);
        return page;
    }

    /**
     * 判断设备是否为待鉴定候选
     */
    private boolean isCandidate(ScrapCandidateVO vo, BigDecimal lifeThreshold, BigDecimal residualThreshold, Integer faultThreshold) {
        // 寿命超过警戒线（已使用年限/设计寿命 > 80%）
        if (vo.getUsedYears() != null && vo.getDesignLifeYears() != null) {
            BigDecimal usageRate = vo.getUsedYears()
                    .divide(BigDecimal.valueOf(vo.getDesignLifeYears()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            if (usageRate.compareTo(lifeThreshold) >= 0) {
                vo.setAlertType("life");
                vo.setAlertMessage(String.format("寿命超过警戒线: %.1f%% >= %.1f%%", usageRate, lifeThreshold));
                return true;
            }
        }

        // 残值低于警戒线（残值率 < 20%）
        if (vo.getResidualRate() != null) {
            if (vo.getResidualRate().compareTo(residualThreshold) < 0) {
                vo.setAlertType("residual");
                vo.setAlertMessage(String.format("残值低于警戒线: %.1f%% < %.1f%%", vo.getResidualRate(), residualThreshold));
                return true;
            }
        }

        // 故障次数超过警戒线（累计故障次数 > 3）
        if (vo.getFaultCount() != null && vo.getFaultCount() >= faultThreshold) {
            vo.setAlertType("fault");
            vo.setAlertMessage(String.format("故障次数超过警戒线: %d >= %d", vo.getFaultCount(), faultThreshold));
            return true;
        }

        return false;
    }

    /**
     * 计算TCO维修方案
     * 公式：修复费 + 未来保养费 + 未来维修费
     *
     * @param device 设备档案
     * @return TCO维修方案成本
     */
    public BigDecimal calculateTcoRepair(DeviceArchive device) {
        // 获取当前维修费用（最近一次故障）
        BigDecimal currentRepairCost = BigDecimal.ZERO;
        FaultOrder latestFault = faultOrderMapper.selectOne(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getDeviceId, device.getId())
                        .eq(FaultOrder::getStatus, "closed")
                        .orderByDesc(FaultOrder::getCloseDate)
                        .last("LIMIT 1")
        );
        if (latestFault != null && latestFault.getRepairCost() != null) {
            currentRepairCost = latestFault.getRepairCost();
        }

        // 计算剩余寿命
        BigDecimal remainingLife = calculateRemainingLife(device);
        if (remainingLife.compareTo(BigDecimal.ZERO) <= 0) {
            remainingLife = BigDecimal.ONE; // 至少按1年计算
        }

        // 获取设备分类
        DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
        if (category == null) {
            return currentRepairCost;
        }

        // 获取桥梁信息（动态周期系数）
        Bridge bridge = bridgeMapper.selectById(device.getBridgeId());
        BigDecimal alphaBeta = BigDecimal.ONE;
        if (bridge != null) {
            alphaBeta = bridge.getAlphaCoef().multiply(bridge.getBetaCoef());
        }

        // 预计未来保养费用（每年保养费用 × 剩余寿命）
        int standardCycle = category.getMaintainCycleDays();
        int dynamicCycle = (int) Math.ceil(standardCycle * alphaBeta.doubleValue());
        int maintainPerYear = 365 / dynamicCycle;
        BigDecimal avgMaintainCost = getAvgMaintainCost(device.getId());
        BigDecimal futureMaintainCost = avgMaintainCost.multiply(BigDecimal.valueOf(maintainPerYear * remainingLife.intValue()));

        // 预计未来维修费用（历史平均维修频率 × 剩余寿命）
        BigDecimal avgFaultRate = getAvgFaultRate(device.getId());
        BigDecimal avgRepairCost = getAvgRepairCost(device.getId());
        BigDecimal futureRepairCost = avgRepairCost.multiply(avgFaultRate).multiply(remainingLife);

        // TCO维修方案总成本
        BigDecimal tcoRepair = currentRepairCost.add(futureMaintainCost).add(futureRepairCost);

        log.info("TCO维修方案计算: 设备={}, 当前修复费={}, 未来保养费={}, 未来维修费={}, TCO={}",
                device.getDeviceCode(), currentRepairCost, futureMaintainCost, futureRepairCost, tcoRepair);

        return tcoRepair.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算TCO更换方案
     * 公式：新设备价 + 未来保养费 - 残值
     *
     * @param device 设备档案
     * @return TCO更换方案成本
     */
    public BigDecimal calculateTcoReplace(DeviceArchive device) {
        // 新设备购置价（当前购置成本）
        BigDecimal newDevicePrice = device.getPurchaseCost();

        // 当前设备残值（可抵扣）
        DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
        BigDecimal salvageValue = BigDecimal.ZERO;
        if (category != null) {
            salvageValue = calculateResidualValue(device, category);
        }

        // 计算剩余寿命
        BigDecimal remainingLife = BigDecimal.valueOf(category != null ? category.getDesignLifeYears() : 10);
        if (remainingLife.compareTo(BigDecimal.ZERO) <= 0) {
            remainingLife = BigDecimal.valueOf(10);
        }

        // 新设备预计未来保养费用
        Bridge bridge = bridgeMapper.selectById(device.getBridgeId());
        BigDecimal alphaBeta = BigDecimal.ONE;
        if (bridge != null) {
            alphaBeta = bridge.getAlphaCoef().multiply(bridge.getBetaCoef());
        }

        int standardCycle = category != null ? category.getMaintainCycleDays() : 90;
        int dynamicCycle = (int) Math.ceil(standardCycle * alphaBeta.doubleValue());
        int maintainPerYear = 365 / dynamicCycle;
        BigDecimal avgMaintainCost = getAvgMaintainCost(device.getId());
        BigDecimal newFutureMaintainCost = avgMaintainCost.multiply(BigDecimal.valueOf(maintainPerYear * remainingLife.intValue()));

        // TCO更换方案总成本 = 新设备价 + 未来保养费 - 残值
        BigDecimal tcoReplace = newDevicePrice.add(newFutureMaintainCost).subtract(salvageValue);

        log.info("TCO更换方案计算: 设备={}, 新设备价={}, 未来保养费={}, 残值抵扣={}, TCO={}",
                device.getDeviceCode(), newDevicePrice, newFutureMaintainCost, salvageValue, tcoReplace);

        return tcoReplace.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 生成推荐结论
     *
     * @param tcoRepair TCO维修方案成本
     * @param tcoReplace TCO更换方案成本
     * @return 推荐结论: repair/replace
     */
    public String generateRecommendation(BigDecimal tcoRepair, BigDecimal tcoReplace) {
        // TCO比较：维修方案成本 < 更换方案成本 → 推荐维修
        if (tcoRepair.compareTo(tcoReplace) < 0) {
            return "repair";
        } else {
            return "replace";
        }
    }

    /**
     * 获取TCO决策面板数据（三栏决策面板）
     *
     * @param deviceId 设备ID
     * @return TCO决策面板数据
     */
    public TcoDecisionVO getTcoDecision(String deviceId) {
        DeviceArchive device = deviceArchiveMapper.selectById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
        Bridge bridge = bridgeMapper.selectById(device.getBridgeId());

        // 获取阈值配置
        BigDecimal residualThreshold = getConfigValue("residual_threshold", new BigDecimal("20"));
        BigDecimal repairThreshold = getConfigValue("repair_threshold", new BigDecimal("30"));

        TcoDecisionVO vo = new TcoDecisionVO();
        vo.setDeviceId(deviceId);
        vo.setDeviceCode(device.getDeviceCode());
        vo.setDeviceName(device.getDeviceName());
        vo.setResidualThreshold(residualThreshold);
        vo.setRepairThreshold(repairThreshold);

        // 当前残值
        BigDecimal currentResidualValue = calculateResidualValue(device, category);
        vo.setCurrentResidualValue(currentResidualValue);

        // 残值率
        if (device.getPurchaseCost() != null && device.getPurchaseCost().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal residualRate = currentResidualValue
                    .divide(device.getPurchaseCost(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            vo.setBelowResidualThreshold(residualRate.compareTo(residualThreshold) < 0);
        }

        // 计算TCO维修方案
        BigDecimal tcoRepair = calculateTcoRepair(device);
        vo.setTcoRepair(tcoRepair);

        // 获取当前维修费用
        FaultOrder latestFault = faultOrderMapper.selectOne(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getDeviceId, deviceId)
                        .eq(FaultOrder::getStatus, "closed")
                        .orderByDesc(FaultOrder::getCloseDate)
                        .last("LIMIT 1")
        );
        if (latestFault != null && latestFault.getRepairCost() != null) {
            vo.setCurrentRepairCost(latestFault.getRepairCost());
            // 判断是否超过修复警戒线
            BigDecimal repairRate = latestFault.getRepairCost()
                    .divide(device.getPurchaseCost(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            vo.setAboveRepairThreshold(repairRate.compareTo(repairThreshold) >= 0);
        }

        // 计算TCO更换方案
        BigDecimal tcoReplace = calculateTcoReplace(device);
        vo.setTcoReplace(tcoReplace);
        vo.setNewDevicePrice(device.getPurchaseCost());
        vo.setSalvageValue(currentResidualValue);

        // 推荐结论
        String recommendation = generateRecommendation(tcoRepair, tcoReplace);
        vo.setRecommendation(recommendation);
        vo.setRecommendationName("repair".equals(recommendation) ? "推荐维修" : "推荐更换");

        // TCO差值
        vo.setTcoDifference(tcoRepair.subtract(tcoReplace));

        // 决策依据说明
        String decisionReason = String.format(
                "维修方案TCO=%.2f元，更换方案TCO=%.2f元，差额=%.2f元。%s",
                tcoRepair, tcoReplace, vo.getTcoDifference(),
                "repair".equals(recommendation) ? "维修方案成本更低，推荐继续维修使用。" : "更换方案成本更低，建议报废更换新设备。"
        );
        vo.setDecisionReason(decisionReason);

        // 方案明细
        vo.setRepairPlanDetail(String.format("修复费 + 预计未来保养费 + 预计未来维修费 = %.2f元", tcoRepair));
        vo.setReplacePlanDetail(String.format("新设备价 + 预计未来保养费 - 残值抵扣 = %.2f元", tcoReplace));

        return vo;
    }

    /**
     * 提交鉴定结论
     *
     * @param decisionDTO 鉴定结论请求
     * @return 鉴定记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ScrapDecision submitDecision(ScrapDecisionDTO decisionDTO) {
        DeviceArchive device = deviceArchiveMapper.selectById(decisionDTO.getDeviceId());
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        // 创建鉴定记录
        ScrapDecision decision = new ScrapDecision();
        decision.setDeviceId(decisionDTO.getDeviceId());
        decision.setTcoRepair(decisionDTO.getTcoRepair());
        decision.setTcoReplace(decisionDTO.getTcoReplace());
        decision.setRecommendation(decisionDTO.getRecommendation());
        decision.setConclusionNotes(decisionDTO.getConclusionNotes());
        decision.setDecider(decisionDTO.getDecider());
        decision.setDecisionDate(LocalDate.now());
        decision.setStatus("pending_approval");

        scrapDecisionMapper.insert(decision);
        log.info("鉴定结论提交成功: 设备={}, 结论={}", device.getDeviceCode(), decisionDTO.getRecommendation());

        return decision;
    }

    /**
     * 查询鉴定历史
     *
     * @param deviceId 设备ID
     * @return 鉴定历史列表
     */
    public List<ScrapDecision> getDecisionHistory(String deviceId) {
        return scrapDecisionMapper.selectList(
                new LambdaQueryWrapper<ScrapDecision>()
                        .eq(ScrapDecision::getDeviceId, deviceId)
                        .orderByDesc(ScrapDecision::getDecisionDate)
        );
    }

    /**
     * 报废审批
     *
     * @param decisionId 鉴定ID
     * @param approveUser 审批人
     * @param approveResult 审批结果
     * @param approveNotes 审批备注
     * @return 鉴定记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ScrapDecision approveDecision(String decisionId, String approveUser, String approveResult, String approveNotes) {
        ScrapDecision decision = scrapDecisionMapper.selectById(decisionId);
        if (decision == null) {
            throw new RuntimeException("鉴定记录不存在");
        }

        // 更新鉴定状态
        decision.setStatus(approveResult);
        scrapDecisionMapper.updateById(decision);

        // 创建审批记录
        ScrapApproval approval = new ScrapApproval();
        approval.setDecisionId(decisionId);
        approval.setApproveUser(approveUser);
        approval.setApproveDate(LocalDate.now());
        approval.setApproveResult(approveResult);
        approval.setApproveNotes(approveNotes);
        scrapApprovalMapper.insert(approval);

        // 如果审批通过且结论为更换，更新设备状态为报废
        if ("approved".equals(approveResult) && "replace".equals(decision.getRecommendation())) {
            DeviceArchive device = deviceArchiveMapper.selectById(decision.getDeviceId());
            if (device != null) {
                device.setStatus("scrapped");
                deviceArchiveMapper.updateById(device);

                // 生成生命周期事件
                LifecycleEvent event = new LifecycleEvent();
                event.setDeviceId(device.getId());
                event.setEventType("scrap");
                event.setTitle("设备报废");
                event.setDescription(String.format("设备 %s 报废处理，残值 %.2f 元", device.getDeviceName(), decision.getTcoReplace()));
                event.setEventTime(java.time.LocalDateTime.now());
                lifecycleEventMapper.insert(event);
            }
        }

        log.info("报废审批完成: 鉴定ID={}, 结果={}", decisionId, approveResult);

        return decision;
    }

    // ==================== 辅助方法 ====================

    /**
     * 计算残值
     */
    private BigDecimal calculateResidualValue(DeviceArchive device, DeviceCategory category) {
        if (device.getPurchaseCost() == null || category.getDesignLifeYears() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal usedYears = BigDecimal.ZERO;
        if (device.getInUseDate() != null) {
            long days = ChronoUnit.DAYS.between(device.getInUseDate(), LocalDate.now());
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
     * 计算剩余寿命
     */
    private BigDecimal calculateRemainingLife(DeviceArchive device) {
        DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
        if (category == null || device.getInUseDate() == null) {
            return BigDecimal.ZERO;
        }

        long days = ChronoUnit.DAYS.between(device.getInUseDate(), LocalDate.now());
        BigDecimal usedYears = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        BigDecimal designLife = BigDecimal.valueOf(category.getDesignLifeYears());

        return designLife.subtract(usedYears).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取平均保养费用
     */
    private BigDecimal getAvgMaintainCost(String deviceId) {
        List<MaintainRecord> records = maintainRecordMapper.selectList(
                new LambdaQueryWrapper<MaintainRecord>()
                        .eq(MaintainRecord::getDeviceId, deviceId)
        );
        if (records.isEmpty()) {
            return new BigDecimal("500"); // 默认500元/次
        }
        BigDecimal totalCost = records.stream()
                .map(MaintainRecord::getActualCost)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalCost.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * 获取平均故障频率（次/年）
     */
    private BigDecimal getAvgFaultRate(String deviceId) {
        Long faultCount = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getDeviceId, deviceId)
        );
        DeviceArchive device = deviceArchiveMapper.selectById(deviceId);
        if (device == null || device.getInUseDate() == null) {
            return BigDecimal.ONE;
        }
        long days = ChronoUnit.DAYS.between(device.getInUseDate(), LocalDate.now());
        BigDecimal years = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        if (years.compareTo(BigDecimal.ZERO) <= 0) {
            years = BigDecimal.ONE;
        }
        return BigDecimal.valueOf(faultCount).divide(years, 2, RoundingMode.HALF_UP);
    }

    /**
     * 获取平均维修费用
     */
    private BigDecimal getAvgRepairCost(String deviceId) {
        List<FaultOrder> orders = faultOrderMapper.selectList(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getDeviceId, deviceId)
                        .eq(FaultOrder::getStatus, "closed")
        );
        if (orders.isEmpty()) {
            return new BigDecimal("2000"); // 默认2000元/次
        }
        BigDecimal totalCost = orders.stream()
                .map(FaultOrder::getRepairCost)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalCost.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * 获取配置值
     */
    private BigDecimal getConfigValue(String key, BigDecimal defaultValue) {
        SysConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, key)
        );
        if (config != null) {
            try {
                return new BigDecimal(config.getConfigValue());
            } catch (Exception e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 获取配置整数值
     */
    private Integer getConfigIntValue(String key, Integer defaultValue) {
        SysConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, key)
        );
        if (config != null) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (Exception e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 转换设备为候选VO
     */
    private ScrapCandidateVO convertToCandidateVO(
            DeviceArchive device,
            Map<String, DeviceCategory> categoryMap,
            Map<String, Bridge> bridgeMap,
            Map<String, Long> faultCountMap,
            Map<String, BigDecimal> repairCostMap,
            Map<String, BigDecimal> maintainCostMap
    ) {
        ScrapCandidateVO vo = new ScrapCandidateVO();
        vo.setDeviceId(device.getId());
        vo.setDeviceCode(device.getDeviceCode());
        vo.setDeviceName(device.getDeviceName());
        vo.setPurchaseCost(device.getPurchaseCost());
        vo.setInUseDate(device.getInUseDate());

        // 获取分类信息
        DeviceCategory category = categoryMap.get(device.getCategory());
        if (category != null) {
            vo.setCategoryName(category.getName());
            vo.setDesignLifeYears(category.getDesignLifeYears());
            vo.setResidualValue(calculateResidualValue(device, category));

            // 计算残值率
            if (device.getPurchaseCost() != null && device.getPurchaseCost().compareTo(BigDecimal.ZERO) > 0) {
                vo.setResidualRate(vo.getResidualValue()
                        .divide(device.getPurchaseCost(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)));
            }
        }

        // 获取桥梁信息
        Bridge bridge = bridgeMap.get(device.getBridgeId());
        if (bridge != null) {
            vo.setBridgeName(bridge.getBridgeName());
        }

        // 计算已使用年限
        if (device.getInUseDate() != null) {
            long days = ChronoUnit.DAYS.between(device.getInUseDate(), LocalDate.now());
            vo.setUsedYears(BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP));
        }

        // 统计故障次数和费用
        Long faultCount = faultCountMap.getOrDefault(device.getId(), 0L);
        vo.setFaultCount(faultCount.intValue());

        BigDecimal totalRepairCost = repairCostMap.getOrDefault(device.getId(), BigDecimal.ZERO);
        vo.setTotalRepairCost(totalRepairCost);

        // 统计保养费用
        BigDecimal totalMaintainCost = maintainCostMap.getOrDefault(device.getId(), BigDecimal.ZERO);
        vo.setTotalMaintainCost(totalMaintainCost);

        // 累计TCO
        BigDecimal purchaseCost = Objects.requireNonNullElse(device.getPurchaseCost(), BigDecimal.ZERO);
        vo.setTotalTco(purchaseCost.add(totalRepairCost).add(totalMaintainCost));

        return vo;
    }
}

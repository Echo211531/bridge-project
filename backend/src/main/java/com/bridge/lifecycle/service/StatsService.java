package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bridge.lifecycle.entity.*;
import com.bridge.lifecycle.mapper.*;
import com.bridge.lifecycle.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计分析服务类（8项关键率）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final DeviceArchiveMapper deviceArchiveMapper;
    private final FaultOrderMapper faultOrderMapper;
    private final MaintainPlanMapper maintainPlanMapper;
    private final MaintainRecordMapper maintainRecordMapper;
    private final DeviceCategoryMapper deviceCategoryMapper;
    private final LifecycleEventMapper lifecycleEventMapper;

    /**
     * 设备在用率计算
     *
     * @return 在用率(百分比)
     */
    public BigDecimal calculateInUseRate() {
        Long totalDevices = deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
        );

        Long inUseDevices = deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
                        .eq(DeviceArchive::getStatus, "in_use")
        );

        if (totalDevices == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(inUseDevices)
                .divide(BigDecimal.valueOf(totalDevices), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 设备完好率计算
     *
     * @return 完好率(百分比)
     */
    public BigDecimal calculateGoodConditionRate() {
        Long totalDevices = deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
        );

        Long goodDevices = deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
                        .ne(DeviceArchive::getStatus, "fault")
                        .ne(DeviceArchive::getStatus, "scrapped")
                        .ne(DeviceArchive::getStatus, "disabled")
        );

        if (totalDevices == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(goodDevices)
                .divide(BigDecimal.valueOf(totalDevices), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 年故障率计算
     *
     * @param year 年份
     * @return 故障率(百分比)
     */
    public BigDecimal calculateFaultRate(int year) {
        Long faultCount = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .ge(FaultOrder::getReportDate, LocalDate.of(year, 1, 1))
                        .le(FaultOrder::getReportDate, LocalDate.of(year, 12, 31))
        );

        Long totalDevices = deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
        );

        if (totalDevices == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(faultCount)
                .divide(BigDecimal.valueOf(totalDevices), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 保养完成率计算
     *
     * @param year 年份
     * @return 完成率(百分比)
     */
    public BigDecimal calculateMaintainCompletionRate(int year) {
        Long totalPlans = maintainPlanMapper.selectCount(
                new LambdaQueryWrapper<MaintainPlan>()
                        .ge(MaintainPlan::getPlanDate, LocalDate.of(year, 1, 1))
                        .le(MaintainPlan::getPlanDate, LocalDate.of(year, 12, 31))
        );

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
                .divide(BigDecimal.valueOf(totalPlans), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 工单关闭率计算
     *
     * @param year 年份
     * @return 关闭率(百分比)
     */
    public BigDecimal calculateOrderCloseRate(int year) {
        Long totalOrders = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .ge(FaultOrder::getReportDate, LocalDate.of(year, 1, 1))
                        .le(FaultOrder::getReportDate, LocalDate.of(year, 12, 31))
        );

        Long closedOrders = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .ge(FaultOrder::getReportDate, LocalDate.of(year, 1, 1))
                        .le(FaultOrder::getReportDate, LocalDate.of(year, 12, 31))
                        .eq(FaultOrder::getStatus, "closed")
        );

        if (totalOrders == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(closedOrders)
                .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * MTBF计算（平均故障间隔时间）
     *
     * @param year 年份
     * @return MTBF(小时)
     */
    public BigDecimal calculateMTBF(int year) {
        // 获取本年所有已关闭的故障工单
        List<FaultOrder> orders = faultOrderMapper.selectList(
                new LambdaQueryWrapper<FaultOrder>()
                        .ge(FaultOrder::getReportDate, LocalDate.of(year, 1, 1))
                        .le(FaultOrder::getReportDate, LocalDate.of(year, 12, 31))
                        .eq(FaultOrder::getStatus, "closed")
                        .orderByAsc(FaultOrder::getReportDate)
        );

        if (orders.size() < 2) {
            return BigDecimal.ZERO;
        }

        // 计算平均故障间隔天数
        long totalDays = 0;
        for (int i = 1; i < orders.size(); i++) {
            if (orders.get(i - 1).getCloseDate() != null && orders.get(i).getReportDate() != null) {
                totalDays += java.time.temporal.ChronoUnit.DAYS.between(
                        orders.get(i - 1).getCloseDate(), orders.get(i).getReportDate());
            }
        }

        // 转换为小时
        BigDecimal mtbf = BigDecimal.valueOf(totalDays * 24)
                .divide(BigDecimal.valueOf(orders.size() - 1), 2, RoundingMode.HALF_UP);

        return mtbf;
    }

    /**
     * MTTR计算（平均修复时间）
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

        long totalDays = closedOrders.stream()
                .mapToLong(o -> java.time.temporal.ChronoUnit.DAYS.between(o.getReportDate(), o.getCloseDate()))
                .sum();

        BigDecimal mttr = BigDecimal.valueOf(totalDays * 24)
                .divide(BigDecimal.valueOf(closedOrders.size()), 2, RoundingMode.HALF_UP);

        return mttr;
    }

    /**
     * 累计运维成本计算
     *
     * @return 累计成本
     */
    public BigDecimal calculateTotalOperationCost() {
        // 累计保养费用
        BigDecimal totalMaintainCost = maintainRecordMapper.selectList(
                new LambdaQueryWrapper<MaintainRecord>()
        ).stream()
                .map(MaintainRecord::getActualCost)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 累计维修费用
        BigDecimal totalRepairCost = faultOrderMapper.selectList(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getStatus, "closed")
        ).stream()
                .map(FaultOrder::getRepairCost)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalMaintainCost.add(totalRepairCost);
    }

    /**
     * 30天费用趋势查询
     *
     * @return 费用趋势列表
     */
    public List<DailyCostVO> get30DayCostTrend() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(30);

        List<DailyCostVO> trend = new ArrayList<>();
        for (int i = 0; i <= 30; i++) {
            LocalDate date = startDate.plusDays(i);

            DailyCostVO daily = new DailyCostVO();
            daily.setDate(date);

            // 当天保养费用
            BigDecimal maintainCost = maintainRecordMapper.selectList(
                    new LambdaQueryWrapper<MaintainRecord>()
                            .eq(MaintainRecord::getRecordDate, date)
            ).stream()
                    .map(MaintainRecord::getActualCost)
                    .filter(c -> c != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            daily.setMaintainCost(maintainCost);

            // 当天维修费用（关闭的工单）
            BigDecimal repairCost = faultOrderMapper.selectList(
                    new LambdaQueryWrapper<FaultOrder>()
                            .eq(FaultOrder::getCloseDate, date)
                            .eq(FaultOrder::getStatus, "closed")
            ).stream()
                    .map(FaultOrder::getRepairCost)
                    .filter(c -> c != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            daily.setRepairCost(repairCost);

            daily.setTotalCost(maintainCost.add(repairCost));
            trend.add(daily);
        }

        return trend;
    }

    /**
     * 待办事项查询
     *
     * @return 待办事项列表
     */
    public List<TodoItemVO> getTodoItems() {
        List<TodoItemVO> items = new ArrayList<>();

        // 待保养计划
        Long pendingMaintains = maintainPlanMapper.selectCount(
                new LambdaQueryWrapper<MaintainPlan>()
                        .eq(MaintainPlan::getStatus, "pending")
                        .le(MaintainPlan::getPlanDate, LocalDate.now().plusDays(7))
        );
        if (pendingMaintains > 0) {
            TodoItemVO item = new TodoItemVO();
            item.setType("maintain");
            item.setTypeName("待保养");
            item.setTitle("有 " + pendingMaintains + " 个设备即将需要保养");
            item.setUrgency("high");
            item.setCount(pendingMaintains.intValue());
            items.add(item);
        }

        // 超期保养
        Long overdueMaintains = maintainPlanMapper.selectCount(
                new LambdaQueryWrapper<MaintainPlan>()
                        .eq(MaintainPlan::getStatus, "overdue")
        );
        if (overdueMaintains > 0) {
            TodoItemVO item = new TodoItemVO();
            item.setType("overdue_maintain");
            item.setTypeName("超期保养");
            item.setTitle("有 " + overdueMaintains + " 个设备保养已超期");
            item.setUrgency("urgent");
            item.setCount(overdueMaintains.intValue());
            items.add(item);
        }

        // 待处理故障工单
        Long openOrders = faultOrderMapper.selectCount(
                new LambdaQueryWrapper<FaultOrder>()
                        .eq(FaultOrder::getStatus, "open")
        );
        if (openOrders > 0) {
            TodoItemVO item = new TodoItemVO();
            item.setType("fault");
            item.setTypeName("待处理故障");
            item.setTitle("有 " + openOrders + " 个故障工单待处理");
            item.setUrgency("high");
            item.setCount(openOrders.intValue());
            items.add(item);
        }

        // 待审批报废申请
        Long pendingApprovals = 0L; // 需要ScrapDecisionMapper查询

        return items;
    }

    /**
     * 设备分类分布统计
     *
     * @return 分类分布列表
     */
    public List<CategoryStatsVO> getCategoryDistribution() {
        Long totalDevices = deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
        );

        List<DeviceCategory> categories = deviceCategoryMapper.selectList(
                new LambdaQueryWrapper<DeviceCategory>()
        );

        return categories.stream()
                .map(category -> {
                    Long deviceCount = deviceArchiveMapper.selectCount(
                            new LambdaQueryWrapper<DeviceArchive>()
                                    .eq(DeviceArchive::getCategory, category.getCode())
                    );

                    CategoryStatsVO vo = new CategoryStatsVO();
                    vo.setCategoryCode(category.getCode());
                    vo.setCategoryName(category.getName());
                    vo.setDeviceCount(deviceCount);

                    if (totalDevices > 0) {
                        vo.setPercentage(BigDecimal.valueOf(deviceCount)
                                .divide(BigDecimal.valueOf(totalDevices), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)));
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 故障排行统计
     *
     * @param limit 限制数量
     * @return 故障排行列表
     */
    public List<FaultRankVO> getFaultRanking(int limit) {
        List<DeviceArchive> devices = deviceArchiveMapper.selectList(
                new LambdaQueryWrapper<DeviceArchive>()
        );

        List<FaultRankVO> ranking = devices.stream()
                .map(device -> {
                    Long faultCount = faultOrderMapper.selectCount(
                            new LambdaQueryWrapper<FaultOrder>()
                                    .eq(FaultOrder::getDeviceId, device.getId())
                    );

                    BigDecimal totalRepairCost = faultOrderMapper.selectList(
                            new LambdaQueryWrapper<FaultOrder>()
                                    .eq(FaultOrder::getDeviceId, device.getId())
                                    .eq(FaultOrder::getStatus, "closed")
                    ).stream()
                            .map(FaultOrder::getRepairCost)
                            .filter(c -> c != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    FaultRankVO vo = new FaultRankVO();
                    vo.setDeviceId(device.getId());
                    vo.setDeviceCode(device.getDeviceCode());
                    vo.setDeviceName(device.getDeviceName());
                    vo.setFaultCount(faultCount.intValue());
                    vo.setTotalRepairCost(totalRepairCost);

                    return vo;
                })
                .filter(vo -> vo.getFaultCount() > 0)
                .sorted(Comparator.comparing(FaultRankVO::getFaultCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // 设置排名
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setRank(i + 1);
        }

        return ranking;
    }

    /**
     * TCO对比统计
     *
     * @return TCO对比列表
     */
    public List<TcoCompareVO> getTcoComparison() {
        List<DeviceCategory> categories = deviceCategoryMapper.selectList(
                new LambdaQueryWrapper<DeviceCategory>()
        );

        return categories.stream()
                .map(category -> {
                    List<DeviceArchive> devices = deviceArchiveMapper.selectList(
                            new LambdaQueryWrapper<DeviceArchive>()
                                    .eq(DeviceArchive::getCategory, category.getCode())
                    );

                    if (devices.isEmpty()) {
                        return null;
                    }

                    BigDecimal avgPurchaseCost = devices.stream()
                            .map(DeviceArchive::getPurchaseCost)
                            .filter(c -> c != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(devices.size()), 2, RoundingMode.HALF_UP);

                    BigDecimal avgMaintainCost = BigDecimal.ZERO;
                    BigDecimal avgRepairCost = BigDecimal.ZERO;

                    for (DeviceArchive device : devices) {
                        BigDecimal maintainCost = maintainRecordMapper.selectList(
                                new LambdaQueryWrapper<MaintainRecord>()
                                        .eq(MaintainRecord::getDeviceId, device.getId())
                        ).stream()
                                .map(MaintainRecord::getActualCost)
                                .filter(c -> c != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        avgMaintainCost = avgMaintainCost.add(maintainCost);

                        BigDecimal repairCost = faultOrderMapper.selectList(
                                new LambdaQueryWrapper<FaultOrder>()
                                        .eq(FaultOrder::getDeviceId, device.getId())
                                        .eq(FaultOrder::getStatus, "closed")
                        ).stream()
                                .map(FaultOrder::getRepairCost)
                                .filter(c -> c != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        avgRepairCost = avgRepairCost.add(repairCost);
                    }

                    avgMaintainCost = avgMaintainCost.divide(BigDecimal.valueOf(devices.size()), 2, RoundingMode.HALF_UP);
                    avgRepairCost = avgRepairCost.divide(BigDecimal.valueOf(devices.size()), 2, RoundingMode.HALF_UP);
                    BigDecimal avgTco = avgPurchaseCost.add(avgMaintainCost).add(avgRepairCost);

                    TcoCompareVO vo = new TcoCompareVO();
                    vo.setCategoryCode(category.getCode());
                    vo.setCategoryName(category.getName());
                    vo.setAvgPurchaseCost(avgPurchaseCost);
                    vo.setAvgMaintainCost(avgMaintainCost);
                    vo.setAvgRepairCost(avgRepairCost);
                    vo.setAvgTco(avgTco);
                    vo.setDeviceCount((long) devices.size());

                    return vo;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
    }

    /**
     * 获取仪表盘完整数据
     *
     * @return 仪表盘数据
     */
    public DashboardVO getDashboard() {
        DashboardVO dashboard = new DashboardVO();

        // 概览统计
        StatsOverviewVO overview = new StatsOverviewVO();
        overview.setTotalDevices(deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
        ));
        overview.setInUseRate(calculateInUseRate());
        overview.setGoodConditionRate(calculateGoodConditionRate());
        overview.setFaultRate(calculateFaultRate(LocalDate.now().getYear()));
        overview.setMaintainCompletionRate(calculateMaintainCompletionRate(LocalDate.now().getYear()));
        overview.setOrderCloseRate(calculateOrderCloseRate(LocalDate.now().getYear()));
        overview.setMtbf(calculateMTBF(LocalDate.now().getYear()));
        overview.setMttr(calculateMTTR(LocalDate.now().getYear()));
        overview.setTotalOperationCost(calculateTotalOperationCost());
        dashboard.setOverview(overview);

        // 费用趋势
        dashboard.setCostTrend(get30DayCostTrend());

        // 分类分布
        dashboard.setCategoryDistribution(getCategoryDistribution());

        // 故障排行
        dashboard.setFaultRanking(getFaultRanking(10));

        // 待办事项
        dashboard.setTodoItems(getTodoItems());

        // TCO对比
        dashboard.setTcoComparison(getTcoComparison());

        return dashboard;
    }
}
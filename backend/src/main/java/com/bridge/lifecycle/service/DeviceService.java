package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.dto.DeviceDTO;
import com.bridge.lifecycle.dto.DeviceUpdateDTO;
import com.bridge.lifecycle.entity.*;
import com.bridge.lifecycle.mapper.*;
import com.bridge.lifecycle.vo.DeviceDetailVO;
import com.bridge.lifecycle.vo.DeviceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备档案管理服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceArchiveMapper deviceArchiveMapper;
    private final DeviceCategoryMapper deviceCategoryMapper;
    private final BridgeMapper bridgeMapper;
    private final LifecycleEventMapper lifecycleEventMapper;

    // 状态名称映射
    private static final java.util.Map<String, String> STATUS_NAME_MAP = java.util.Map.of(
            "in_use", "在用",
            "in_stock", "库存",
            "maintaining", "保养中",
            "fault", "故障",
            "scrapped", "已报废",
            "disabled", "禁用"
    );

    // 市场折扣系数（设备类型影响）
    private static final java.util.Map<String, BigDecimal> MARKET_DISCOUNT_MAP = java.util.Map.of(
            "sensor", new BigDecimal("0.85"),
            "camera", new BigDecimal("0.80"),
            "communication", new BigDecimal("0.75"),
            "server", new BigDecimal("0.70"),
            "software", new BigDecimal("0.50")
    );

    /**
     * 生成设备编码
     * 规则：DEV-类别-桥梁-序号
     *
     * @param category 设备分类编码
     * @param bridgeId 桥梁ID
     * @return 设备编码
     */
    public String generateDeviceCode(String category, String bridgeId) {
        // 获取桥梁编码
        Bridge bridge = bridgeMapper.selectById(bridgeId);
        if (bridge == null) {
            throw new RuntimeException("桥梁不存在");
        }

        // 查询该桥梁该类别下的设备数量
        Long count = deviceArchiveMapper.selectCount(
                new LambdaQueryWrapper<DeviceArchive>()
                        .eq(DeviceArchive::getCategory, category)
                        .eq(DeviceArchive::getBridgeId, bridgeId)
        );

        // 生成序号（3位）
        String seq = String.format("%03d", count + 1);

        return String.format("DEV-%s-%s-%s", category.toUpperCase(), bridge.getBridgeCode(), seq);
    }

    /**
     * 计算残值估算
     * 公式：直线折旧 × 市场折扣
     *
     * @param device 设备档案
     * @param category 设备分类
     * @return 残值估算
     */
    public BigDecimal calculateResidualValue(DeviceArchive device, DeviceCategory category) {
        if (device.getPurchaseCost() == null || category.getDesignLifeYears() == null) {
            return BigDecimal.ZERO;
        }

        // 计算已使用年限
        BigDecimal usedYears = BigDecimal.ZERO;
        if (device.getInUseDate() != null) {
            long days = ChronoUnit.DAYS.between(device.getInUseDate(), LocalDate.now());
            usedYears = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        }

        // 设计寿命
        BigDecimal designLife = BigDecimal.valueOf(category.getDesignLifeYears());

        // 直线折旧率：已使用年限 / 设计寿命
        BigDecimal depreciationRate = usedYears.divide(designLife, 4, RoundingMode.HALF_UP);
        if (depreciationRate.compareTo(BigDecimal.ONE) > 0) {
            depreciationRate = BigDecimal.ONE;
        }

        // 直线折旧后的价值
        BigDecimal depreciatedValue = device.getPurchaseCost()
                .multiply(BigDecimal.ONE.subtract(depreciationRate));

        // 市场折扣系数
        BigDecimal marketDiscount = MARKET_DISCOUNT_MAP.getOrDefault(device.getCategory(), new BigDecimal("0.70"));

        // 残值 = 折旧价值 × 市场折扣
        BigDecimal residualValue = depreciatedValue.multiply(marketDiscount)
                .setScale(2, RoundingMode.HALF_UP);

        return residualValue;
    }

    /**
     * 设备列表分页查询
     *
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @param category  分类筛选
     * @param bridgeId  桥梁筛选
     * @param status    状态筛选
     * @return 分页设备列表
     */
    public Page<DeviceVO> listDevices(Integer pageNum, Integer pageSize, String category, String bridgeId, String status) {
        Page<DeviceArchive> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DeviceArchive> wrapper = new LambdaQueryWrapper<>();

        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(DeviceArchive::getCategory, category);
        }
        // 桥梁筛选
        if (StringUtils.hasText(bridgeId)) {
            wrapper.eq(DeviceArchive::getBridgeId, bridgeId);
        }
        // 状态筛选
        if (StringUtils.hasText(status)) {
            wrapper.eq(DeviceArchive::getStatus, status);
        }
        // 按创建时间降序
        wrapper.orderByDesc(DeviceArchive::getCreateTime);

        Page<DeviceArchive> devicePage = deviceArchiveMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<DeviceVO> voPage = new Page<>(devicePage.getCurrent(), devicePage.getSize(), devicePage.getTotal());
        List<DeviceVO> voList = devicePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 设备详情查询（含残值计算）
     *
     * @param id 设备ID
     * @return 设备详情
     */
    public DeviceDetailVO getDeviceDetail(String id) {
        DeviceArchive device = deviceArchiveMapper.selectById(id);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }
        return convertToDetailVO(device);
    }

    /**
     * 创建设备（自动生成生命周期事件）
     *
     * @param deviceDTO 设备创建请求
     * @return 设备信息
     */
    @Transactional(rollbackFor = Exception.class)
    public DeviceVO createDevice(DeviceDTO deviceDTO) {
        // 检查分类和桥梁是否存在
        DeviceCategory category = deviceCategoryMapper.selectById(deviceDTO.getCategory());
        if (category == null) {
            throw new RuntimeException("设备分类不存在");
        }

        Bridge bridge = bridgeMapper.selectById(deviceDTO.getBridgeId());
        if (bridge == null) {
            throw new RuntimeException("桥梁不存在");
        }

        // 创建设备实体
        DeviceArchive device = new DeviceArchive();
        device.setDeviceCode(generateDeviceCode(deviceDTO.getCategory(), deviceDTO.getBridgeId()));
        device.setDeviceName(deviceDTO.getDeviceName());
        device.setCategory(deviceDTO.getCategory());
        device.setBridgeId(deviceDTO.getBridgeId());
        device.setPurchaseCost(deviceDTO.getPurchaseCost());
        device.setManufacturer(deviceDTO.getManufacturer());
        device.setModel(deviceDTO.getModel());
        device.setPurchaseDate(deviceDTO.getPurchaseDate());
        device.setInUseDate(deviceDTO.getInUseDate());
        device.setWarrantyYears(deviceDTO.getWarrantyYears());
        device.setLocationOnBridge(deviceDTO.getLocationOnBridge());
        device.setStatus("in_stock");  // 默认状态为库存

        deviceArchiveMapper.insert(device);
        log.info("创建设备成功: {}", device.getDeviceCode());

        // 自动生成生命周期事件：采购入库
        createLifecycleEvent(device, "purchase", "采购入库",
                String.format("设备 %s 采购入库，成本 %.2f 元", device.getDeviceName(), device.getPurchaseCost()),
                device.getPurchaseCost());

        // 如果有投入使用日期，生成投入使用事件
        if (deviceDTO.getInUseDate() != null) {
            createLifecycleEvent(device, "commission", "投入使用",
                    String.format("设备 %s 投入使用，位置: %s", device.getDeviceName(), deviceDTO.getLocationOnBridge()),
                    BigDecimal.ZERO);
            device.setStatus("in_use");
            deviceArchiveMapper.updateById(device);
        }

        return convertToVO(device);
    }

    /**
     * 更新设备
     *
     * @param deviceUpdateDTO 设备更新请求
     * @return 设备信息
     */
    @Transactional(rollbackFor = Exception.class)
    public DeviceVO updateDevice(DeviceUpdateDTO deviceUpdateDTO) {
        DeviceArchive device = deviceArchiveMapper.selectById(deviceUpdateDTO.getId());
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        // 更新字段
        if (StringUtils.hasText(deviceUpdateDTO.getDeviceName())) {
            device.setDeviceName(deviceUpdateDTO.getDeviceName());
        }
        if (StringUtils.hasText(deviceUpdateDTO.getManufacturer())) {
            device.setManufacturer(deviceUpdateDTO.getManufacturer());
        }
        if (StringUtils.hasText(deviceUpdateDTO.getModel())) {
            device.setModel(deviceUpdateDTO.getModel());
        }
        if (StringUtils.hasText(deviceUpdateDTO.getLocationOnBridge())) {
            device.setLocationOnBridge(deviceUpdateDTO.getLocationOnBridge());
        }
        if (StringUtils.hasText(deviceUpdateDTO.getStatus())) {
            device.setStatus(deviceUpdateDTO.getStatus());
        }

        deviceArchiveMapper.updateById(device);
        log.info("更新设备成功: {}", device.getDeviceCode());

        return convertToVO(device);
    }

    /**
     * 超期保养设备查询
     *
     * @return 超期保养设备列表
     */
    public List<DeviceVO> listOverdueDevices() {
        // 查询状态为在用的设备
        List<DeviceArchive> devices = deviceArchiveMapper.selectList(
                new LambdaQueryWrapper<DeviceArchive>()
                        .eq(DeviceArchive::getStatus, "in_use")
        );

        // TODO: 根据保养计划判断是否超期
        return devices.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 设备分类查询
     *
     * @return 设备分类列表
     */
    public List<DeviceCategory> listCategories() {
        return deviceCategoryMapper.selectList(
                new LambdaQueryWrapper<DeviceCategory>()
        );
    }

    /**
     * 设备状态流转
     *
     * @param id     设备ID
     * @param status 新状态
     * @return 设备信息
     */
    @Transactional(rollbackFor = Exception.class)
    public DeviceVO changeStatus(String id, String status) {
        DeviceArchive device = deviceArchiveMapper.selectById(id);
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        String oldStatus = device.getStatus();
        device.setStatus(status);
        deviceArchiveMapper.updateById(device);

        log.info("设备状态变更: {} -> {} -> {}", device.getDeviceCode(), oldStatus, status);

        return convertToVO(device);
    }

    /**
     * 创建生命周期事件
     */
    private void createLifecycleEvent(DeviceArchive device, String eventType, String title, String description, BigDecimal cost) {
        LifecycleEvent event = new LifecycleEvent();
        event.setDeviceId(device.getId());
        event.setEventType(eventType);
        event.setTitle(title);
        event.setDescription(description);
        event.setCost(cost);
        event.setEventTime(java.time.LocalDateTime.now());
        lifecycleEventMapper.insert(event);
    }

    /**
     * 转换实体为VO
     */
    private DeviceVO convertToVO(DeviceArchive device) {
        DeviceVO vo = new DeviceVO();
        vo.setId(device.getId());
        vo.setDeviceCode(device.getDeviceCode());
        vo.setDeviceName(device.getDeviceName());
        vo.setCategory(device.getCategory());
        vo.setBridgeId(device.getBridgeId());
        vo.setStatus(device.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.getOrDefault(device.getStatus(), ""));
        vo.setPurchaseCost(device.getPurchaseCost());
        vo.setManufacturer(device.getManufacturer());
        vo.setModel(device.getModel());
        vo.setInUseDate(device.getInUseDate());
        vo.setPurchaseDate(device.getPurchaseDate());
        vo.setLocationOnBridge(device.getLocationOnBridge());
        vo.setCreateTime(device.getCreateTime());
        vo.setUpdateTime(device.getUpdateTime());

        // 获取分类名称
        DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
        if (category != null) {
            vo.setCategoryName(category.getName());
            vo.setResidualValue(calculateResidualValue(device, category));
        }

        // 获取桥梁名称
        Bridge bridge = bridgeMapper.selectById(device.getBridgeId());
        if (bridge != null) {
            vo.setBridgeName(bridge.getBridgeName());
        }

        return vo;
    }

    /**
     * 转换实体为详情VO
     */
    private DeviceDetailVO convertToDetailVO(DeviceArchive device) {
        DeviceDetailVO vo = new DeviceDetailVO();
        vo.setId(device.getId());
        vo.setDeviceCode(device.getDeviceCode());
        vo.setDeviceName(device.getDeviceName());
        vo.setCategory(device.getCategory());
        vo.setBridgeId(device.getBridgeId());
        vo.setStatus(device.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.getOrDefault(device.getStatus(), ""));
        vo.setPurchaseCost(device.getPurchaseCost());
        vo.setManufacturer(device.getManufacturer());
        vo.setModel(device.getModel());
        vo.setWarrantyYears(device.getWarrantyYears());
        vo.setInUseDate(device.getInUseDate());
        vo.setPurchaseDate(device.getPurchaseDate());
        vo.setLocationOnBridge(device.getLocationOnBridge());
        vo.setCreateTime(device.getCreateTime());
        vo.setUpdateTime(device.getUpdateTime());

        // 获取分类信息
        DeviceCategory category = deviceCategoryMapper.selectById(device.getCategory());
        if (category != null) {
            vo.setCategoryName(category.getName());
            vo.setDesignLifeYears(category.getDesignLifeYears());
            vo.setMaintainCycleDays(category.getMaintainCycleDays());
            vo.setResidualValue(calculateResidualValue(device, category));
        }

        // 获取桥梁信息
        Bridge bridge = bridgeMapper.selectById(device.getBridgeId());
        if (bridge != null) {
            vo.setBridgeName(bridge.getBridgeName());
            vo.setAlphaCoef(bridge.getAlphaCoef());
            vo.setBetaCoef(bridge.getBetaCoef());

            // 计算动态保养周期
            if (category != null && category.getMaintainCycleDays() != null) {
                int dynamicCycle = (int) (category.getMaintainCycleDays() *
                        bridge.getAlphaCoef().doubleValue() *
                        bridge.getBetaCoef().doubleValue());
                vo.setDynamicMaintainCycle(dynamicCycle);
            }
        }

        // 计算残值率和已使用年限
        if (device.getInUseDate() != null && category != null) {
            long days = ChronoUnit.DAYS.between(device.getInUseDate(), LocalDate.now());
            BigDecimal usedYears = BigDecimal.valueOf(days)
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
            vo.setUsedYears(usedYears);

            BigDecimal designLife = BigDecimal.valueOf(category.getDesignLifeYears());
            vo.setRemainingLife(designLife.subtract(usedYears));

            if (vo.getPurchaseCost() != null && vo.getPurchaseCost().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal rate = vo.getResidualValue()
                        .divide(vo.getPurchaseCost(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                vo.setResidualRate(rate);
            }
        }

        return vo;
    }
}
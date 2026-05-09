package com.bridge.lifecycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bridge.lifecycle.dto.PurchaseOrderDTO;
import com.bridge.lifecycle.dto.PurchaseStatusDTO;
import com.bridge.lifecycle.entity.Bridge;
import com.bridge.lifecycle.entity.DeviceCategory;
import com.bridge.lifecycle.entity.PurchaseOrder;
import com.bridge.lifecycle.mapper.BridgeMapper;
import com.bridge.lifecycle.mapper.DeviceCategoryMapper;
import com.bridge.lifecycle.mapper.PurchaseOrderMapper;
import com.bridge.lifecycle.vo.PurchaseOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 采购订单管理服务类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final DeviceCategoryMapper deviceCategoryMapper;
    private final BridgeMapper bridgeMapper;

    // 状态名称映射
    private static final java.util.Map<String, String> STATUS_NAME_MAP = java.util.Map.of(
            "pending", "待发货",
            "shipping", "运输中",
            "received", "已入库"
    );

    /**
     * 生成订单编码
     * 规则：PO-年月-序号
     *
     * @return 订单编码
     */
    public String generateOrderNo() {
        LocalDate now = LocalDate.now();
        String prefix = String.format("PO-%d%02d-", now.getYear(), now.getMonthValue());

        // 查询本月订单数量
        Long count = purchaseOrderMapper.selectCount(
                new LambdaQueryWrapper<PurchaseOrder>()
                        .likeRight(PurchaseOrder::getOrderNo, prefix)
        );

        return String.format("%s%03d", prefix, count + 1);
    }

    /**
     * 订单列表查询
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param status   状态筛选
     * @return 分页订单列表
     */
    public Page<PurchaseOrderVO> listOrders(Integer pageNum, Integer pageSize, String status) {
        Page<PurchaseOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PurchaseOrder::getStatus, status);
        }
        // 按创建时间降序
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);

        Page<PurchaseOrder> orderPage = purchaseOrderMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<PurchaseOrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<PurchaseOrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 订单详情查询
     *
     * @param id 订单ID
     * @return 订单详情
     */
    public PurchaseOrderVO getOrderById(String id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return convertToVO(order);
    }

    /**
     * 订单创建（总金额计算）
     *
     * @param orderDTO 订单创建请求
     * @return 订单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrderVO createOrder(PurchaseOrderDTO orderDTO) {
        // 检查设备分类是否存在
        DeviceCategory category = deviceCategoryMapper.selectById(orderDTO.getCategory());
        if (category == null) {
            throw new RuntimeException("设备分类不存在");
        }

        // 计算总金额
        BigDecimal totalAmount = orderDTO.getUnitPrice()
                .multiply(BigDecimal.valueOf(orderDTO.getQuantity()));

        // 创建订单
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setCategory(orderDTO.getCategory());
        order.setQuantity(orderDTO.getQuantity());
        order.setUnitPrice(orderDTO.getUnitPrice());
        order.setTotalAmount(totalAmount);
        order.setSupplier(orderDTO.getSupplier());
        order.setBridgeId(orderDTO.getBridgeId());
        order.setStatus("pending");

        purchaseOrderMapper.insert(order);
        log.info("创建采购订单成功: 订单号={}, 总金额={}", order.getOrderNo(), totalAmount);

        return convertToVO(order);
    }

    /**
     * 订单状态流转（pending→shipping→received）
     *
     * @param statusDTO 订单状态更新请求
     * @return 订单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public PurchaseOrderVO updateStatus(PurchaseStatusDTO statusDTO) {
        PurchaseOrder order = purchaseOrderMapper.selectById(statusDTO.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 状态流转校验
        String currentStatus = order.getStatus();
        String newStatus = statusDTO.getStatus();

        // pending -> shipping
        if ("pending".equals(currentStatus) && "shipping".equals(newStatus)) {
            order.setStatus(newStatus);
            order.setShipDate(statusDTO.getShipDate() != null ? statusDTO.getShipDate() : LocalDate.now());
        }
        // shipping -> received
        else if ("shipping".equals(currentStatus) && "received".equals(newStatus)) {
            order.setStatus(newStatus);
            order.setReceiveDate(statusDTO.getReceiveDate() != null ? statusDTO.getReceiveDate() : LocalDate.now());
        }
        else {
            throw new RuntimeException("无效的状态流转: " + currentStatus + " -> " + newStatus);
        }

        purchaseOrderMapper.updateById(order);
        log.info("订单状态流转: {} -> {}", order.getOrderNo(), newStatus);

        return convertToVO(order);
    }

    /**
     * 转换实体为VO
     */
    private PurchaseOrderVO convertToVO(PurchaseOrder order) {
        PurchaseOrderVO vo = new PurchaseOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setCategory(order.getCategory());
        vo.setQuantity(order.getQuantity());
        vo.setUnitPrice(order.getUnitPrice());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setSupplier(order.getSupplier());
        vo.setBridgeId(order.getBridgeId());
        vo.setStatus(order.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.getOrDefault(order.getStatus(), ""));
        vo.setShipDate(order.getShipDate());
        vo.setReceiveDate(order.getReceiveDate());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        // 获取设备分类名称
        DeviceCategory category = deviceCategoryMapper.selectById(order.getCategory());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        // 获取桥梁名称
        if (order.getBridgeId() != null) {
            Bridge bridge = bridgeMapper.selectById(order.getBridgeId());
            if (bridge != null) {
                vo.setBridgeName(bridge.getBridgeName());
            }
        }

        return vo;
    }
}
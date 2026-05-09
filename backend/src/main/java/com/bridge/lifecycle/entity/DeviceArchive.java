package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备档案实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("device_archive")
public class DeviceArchive {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备分类编码
     */
    private String category;

    /**
     * 所属桥梁ID
     */
    private String bridgeId;

    /**
     * 状态: in_use/in_stock/maintaining/fault/scrapped/disabled
     */
    private String status;

    /**
     * 购置成本
     */
    private BigDecimal purchaseCost;

    /**
     * 生产厂商
     */
    private String manufacturer;

    /**
     * 型号规格
     */
    private String model;

    /**
     * 投入使用日期
     */
    private LocalDate inUseDate;

    /**
     * 采购日期
     */
    private LocalDate purchaseDate;

    /**
     * 保修年限
     */
    private BigDecimal warrantyYears;

    /**
     * 桥梁位置
     */
    private String locationOnBridge;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
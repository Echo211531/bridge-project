package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 桥梁档案实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("bridge")
public class Bridge {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 桥梁编码
     */
    private String bridgeCode;

    /**
     * 桥梁名称
     */
    private String bridgeName;

    /**
     * 气候带: cold/severe_cold/temperate/humid/coastal
     */
    private String climateZone;

    /**
     * α气候系数
     */
    private BigDecimal alphaCoef;

    /**
     * 年平均日交通量(AADT)
     */
    private Integer aadt;

    /**
     * βAADT系数
     */
    private BigDecimal betaCoef;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 建成年份
     */
    private Integer buildYear;

    /**
     * 桥梁长度(米)
     */
    private BigDecimal length;

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
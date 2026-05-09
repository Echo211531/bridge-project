package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 桥梁详情VO（含AADT历史）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "桥梁详情")
public class BridgeDetailVO {

    /**
     * 桥梁ID
     */
    @Schema(description = "桥梁ID")
    private String id;

    /**
     * 桥梁编码
     */
    @Schema(description = "桥梁编码")
    private String bridgeCode;

    /**
     * 桥梁名称
     */
    @Schema(description = "桥梁名称")
    private String bridgeName;

    /**
     * 气候带
     */
    @Schema(description = "气候带")
    private String climateZone;

    /**
     * 气候带名称
     */
    @Schema(description = "气候带名称")
    private String climateZoneName;

    /**
     * α气候系数
     */
    @Schema(description = "α气候系数")
    private BigDecimal alphaCoef;

    /**
     * 年平均日交通量(AADT)
     */
    @Schema(description = "年平均日交通量")
    private Integer aadt;

    /**
     * βAADT系数
     */
    @Schema(description = "βAADT系数")
    private BigDecimal betaCoef;

    /**
     * 地理位置
     */
    @Schema(description = "地理位置")
    private String location;

    /**
     * 建成年份
     */
    @Schema(description = "建成年份")
    private Integer buildYear;

    /**
     * 桥梁长度(米)
     */
    @Schema(description = "桥梁长度(米)")
    private BigDecimal length;

    /**
     * AADT历史记录
     */
    @Schema(description = "AADT历史记录")
    private List<AadtHistoryVO> aadtHistory;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
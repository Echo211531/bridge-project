package com.bridge.lifecycle.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AADT历史记录VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "AADT历史记录")
public class AadtHistoryVO {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private String id;

    /**
     * 桥梁ID
     */
    @Schema(description = "桥梁ID")
    private String bridgeId;

    /**
     * 旧AADT值
     */
    @Schema(description = "旧AADT值")
    private Integer oldAadt;

    /**
     * 新AADT值
     */
    @Schema(description = "新AADT值")
    private Integer newAadt;

    /**
     * β系数变更
     */
    @Schema(description = "β系数变更")
    private String betaCoefChange;

    /**
     * 变更原因
     */
    @Schema(description = "变更原因")
    private String reason;

    /**
     * 变更时间
     */
    @Schema(description = "变更时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime changeTime;
}
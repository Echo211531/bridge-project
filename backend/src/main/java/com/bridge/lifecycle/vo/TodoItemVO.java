package com.bridge.lifecycle.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 待办事项VO
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@Schema(description = "待办事项")
public class TodoItemVO {

    @Schema(description = "事项类型")
    private String type;

    @Schema(description = "事项类型名称")
    private String typeName;

    @Schema(description = "事项标题")
    private String title;

    @Schema(description = "关联ID")
    private String relatedId;

    @Schema(description = "截止日期")
    private LocalDate deadline;

    @Schema(description = "紧急程度")
    private String urgency;

    @Schema(description = "数量")
    private Integer count;
}
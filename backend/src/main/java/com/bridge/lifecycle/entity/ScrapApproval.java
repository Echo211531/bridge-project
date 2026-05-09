package com.bridge.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 报废审批实体类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
@TableName("scrap_approval")
public class ScrapApproval {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 鉴定ID
     */
    private String decisionId;

    /**
     * 审批人
     */
    private String approveUser;

    /**
     * 审批日期
     */
    private LocalDate approveDate;

    /**
     * 审批结果: approved/rejected
     */
    private String approveResult;

    /**
     * 审批备注
     */
    private String approveNotes;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
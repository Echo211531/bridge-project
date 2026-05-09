package com.bridge.lifecycle.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 编码生成工具类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
public class CodeGenerator {

    /**
     * 年月格式
     */
    private static final DateTimeFormatter YM_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * 生成UUID主键
     */
    public static String generateId() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 生成设备编码
     * 格式: DEV-{类别码}-{桥梁码}-{4位序号}
     * 示例: DEV-LT-BR01-0001
     *
     * @param category   设备分类编码
     * @param bridgeCode 桥梁编码
     * @param sequence   序号
     * @return 设备编码
     */
    public static String generateDeviceCode(String category, String bridgeCode, int sequence) {
        return String.format("DEV-%s-%s-%04d", category, bridgeCode, sequence);
    }

    /**
     * 生成故障工单编码
     * 格式: FLT-{年月}-{序号}
     * 示例: FLT-202605-0001
     *
     * @param date     日期
     * @param sequence 序号
     * @return 工单编码
     */
    public static String generateFaultOrderCode(LocalDate date, int sequence) {
        return String.format("FLT-%s-%04d", date.format(YM_FORMATTER), sequence);
    }

    /**
     * 生成采购订单编码
     * 格式: PO-{年月}-{序号}
     * 示例: PO-202605-0001
     *
     * @param date     日期
     * @param sequence 序号
     * @return 订单编码
     */
    public static String generatePurchaseOrderCode(LocalDate date, int sequence) {
        return String.format("PO-%s-%04d", date.format(YM_FORMATTER), sequence);
    }

    /**
     * 生成桥梁编码
     * 格式: BR{2位序号}
     * 示例: BR01
     *
     * @param sequence 序号
     * @return 桥梁编码
     */
    public static String generateBridgeCode(int sequence) {
        return String.format("BR%02d", sequence);
    }
}
package com.bridge.lifecycle.utils;

import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author 程国忠
 * @since 2026-05-09
 */
public class DateUtils {

    /**
     * 默认日期格式
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 默认时间格式
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化日期
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * 格式化时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * 解析日期字符串
     */
    public static LocalDate parseDate(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
    }

    /**
     * 计算两个日期之间的天数
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期之间的年数
     */
    public static double yearsBetween(LocalDate start, LocalDate end) {
        long days = daysBetween(start, end);
        return days / 365.25;
    }

    /**
     * 获取当前日期
     */
    public static LocalDate now() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间
     */
    public static LocalDateTime nowTime() {
        return LocalDateTime.now();
    }

    /**
     * 判断日期是否超期
     */
    public static boolean isOverdue(LocalDate planDate, LocalDate currentDate) {
        return planDate.isBefore(currentDate);
    }

    /**
     * 获取超期天数
     */
    public static long getOverdueDays(LocalDate planDate, LocalDate currentDate) {
        if (planDate.isBefore(currentDate)) {
            return daysBetween(planDate, currentDate);
        }
        return 0;
    }
}
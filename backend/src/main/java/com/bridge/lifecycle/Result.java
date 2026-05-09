package com.bridge.lifecycle;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应结果类
 *
 * @param <T> 数据类型
 * @author 程国忠
 * @since 2026-05-09
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    /**
     * 失败响应（默认400）
     */
    public static <T> Result<T> error(String message) {
        return error(400, message);
    }

    /**
     * 未授权响应
     */
    public static <T> Result<T> unauthorized() {
        return error(401, "未授权访问");
    }

    /**
     * 禁止访问响应
     */
    public static <T> Result<T> forbidden() {
        return error(403, "禁止访问");
    }
}
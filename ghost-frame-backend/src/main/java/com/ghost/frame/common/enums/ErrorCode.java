package com.ghost.frame.common.enums;

public enum ErrorCode {
    // 200-成功
    SUCCESS(200, "成功"),
    // 400-请求参数错误
    BAD_REQUEST(400, "请求参数错误"),
    // 401-用户未登录
    UNAUTHORIZED(401, "用户未登录"),
    // 500-服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器错误");

    private Integer code;

    private String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

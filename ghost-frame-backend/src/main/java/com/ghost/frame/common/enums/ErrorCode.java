package com.ghost.frame.common.enums;

public enum ErrorCode {
    // 200-成功
    SUCCESS(200, "成功"),
    // 400-请求参数错误
    BAD_REQUEST(400, "请求参数错误"),
    // 401-用户未登录
    UNAUTHORIZED(401, "用户未登录"),
    // 500-服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器错误"),

    /**
     * 用户相关错误码
     */
    USERNAME_EXISTS(400, "用户名已存在"),
    USERNAME_OR_PASSWORD_ERROR(400, "用户名或密码错误"),
    USER_DISABLED(403, "账号已被禁用"),
    USER_NOT_FOUND(404, "用户不存在"),
    NICKNAME_EXISTS(400, "昵称已存在");

    private final Integer code;

    private final String message;

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

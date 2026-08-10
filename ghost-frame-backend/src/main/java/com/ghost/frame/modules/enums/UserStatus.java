package com.ghost.frame.modules.enums;

import java.util.Arrays;

public enum UserStatus {
    NORMAL(1, "正常"),
    LOCKED(0, "禁用");

    /**
     * 枚举值
     */
    private Integer code;

    /**
     * 枚举描述
     */
    private String desc;

    /**
     * 构造函数
     *
     * @param code 枚举值
     * @param desc 枚举描述
     */
    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据枚举值获取枚举
     *
     * @param code 枚举值
     * @return 枚举
     */
    public static UserStatus fromCode(Integer code){
        return Arrays.stream(values())
                .filter(status -> status.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效代码: " + code));
    }

}

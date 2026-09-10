package com.ghost.frame.modules.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.Arrays;

public enum DeleteFlag {
    DELETED(1, "已删除"),
    NOT_DELETED(0, "未删除");
    /**
     * 枚举值
     */
    @EnumValue
    private final Integer code;

    /**
     * 枚举描述
     */
    private final String desc;

    /**
     * 构造函数
     *
     * @param code 枚举值
     * @param desc 枚举描述
     */
    DeleteFlag(Integer code, String desc) {
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
    public static DeleteFlag fromCode(Integer code){
        return Arrays.stream(values())
                .filter(status -> status.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效代码: " + code));
    }
}

package com.ghost.frame.modules.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户角色
 * @author 34592
 */

@Getter
public enum UserRole {
    USER("USER","普通用户"),
    ADMIN("ADMIN","管理员"),
    SUPER_ADMIN("SUPER_ADMIN","超级管理员");

    /**
     * 枚举值
     */
    @EnumValue
    private final String code;

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
    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

  /**
     * 根据枚举值获取枚举
     *
     * @param code 枚举值
     * @return 枚举
     */
    public static UserRole fromCode(String code) {
        return Arrays.stream(values())
                .filter(role -> role.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知角色: " + code));
    }
}

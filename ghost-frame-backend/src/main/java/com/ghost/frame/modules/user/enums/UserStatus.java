package com.ghost.frame.modules.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户状态
 * @author 34592
 */

@Getter
public enum UserStatus {
    NORMAL(1, "正常"),
    LOCKED(0, "禁用");

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
    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
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

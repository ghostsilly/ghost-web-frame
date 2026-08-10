package com.ghost.frame.modules.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author ghost
 * @description 注册请求参数
 */
@Data
public class RegisterRequestDTO {
    /**
     * 用户名
     */
    @NotNull
    private String username;
    /**
     * 密码
     */
    @NotNull
    private String password;
    /**
     * 昵称
     */
    @NotNull
    private String nickname;
}

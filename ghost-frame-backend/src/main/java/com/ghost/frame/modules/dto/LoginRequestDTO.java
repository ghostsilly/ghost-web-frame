package com.ghost.frame.modules.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author ghost
 * @description 登录请求参数
 */
@Data
public class LoginRequestDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}

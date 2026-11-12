package com.ghost.frame.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ghost
 * @description 登录请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录请求参数")
public class LoginRequestDTO {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}

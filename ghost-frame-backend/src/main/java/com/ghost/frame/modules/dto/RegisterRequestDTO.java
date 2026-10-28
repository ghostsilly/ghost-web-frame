package com.ghost.frame.modules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ghost
 * @description 注册请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 20, message = "用户名长度在5到20个字符之间")
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickname;
}

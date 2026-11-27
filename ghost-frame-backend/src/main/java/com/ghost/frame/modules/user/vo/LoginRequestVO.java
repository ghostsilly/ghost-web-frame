package com.ghost.frame.modules.user.vo;

import com.ghost.frame.modules.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 34592
 * @description 登录请求VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestVO {
  //  主键id
  @Schema(description = "主键id")
  private Long id;
  //  昵称
  @Schema(description = "昵称")
  private String nickname;
  //  用户名
  @Schema(description = "用户名")
  private String username;
  //  角色
  @Schema(description = "角色")
  private UserRole role;
  //  token令牌
  @Schema(description = "token令牌")
  private String token;
  //  刷新令牌
  @Schema(description = "刷新令牌")
  private String refreshToken;
}

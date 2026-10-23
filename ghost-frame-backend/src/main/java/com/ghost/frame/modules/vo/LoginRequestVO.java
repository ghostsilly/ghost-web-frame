package com.ghost.frame.modules.vo;

import com.ghost.frame.modules.enums.UserRole;
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
  private Long id;
  //  昵称
  private String nickname;
  //  用户名
  private String username;
  //  角色
  private UserRole role;
  //  token令牌
  private String token;
}

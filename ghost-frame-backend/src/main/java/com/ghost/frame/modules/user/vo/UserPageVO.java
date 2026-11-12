package com.ghost.frame.modules.user.vo;

import com.ghost.frame.modules.user.enums.UserRole;
import com.ghost.frame.modules.user.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 34592
 */
@Data
public class UserPageVO {
  /**
   * id
   */
  private Long id;
  /**
   * 昵称
   */
  private String nickname;
  /**
   * 用户名
   */
  private String username;
  /**
   * 角色
   */
  private UserRole  role;
  /**
   * 状态
   */
  private UserStatus status;
  /**
   * 创建时间
   */
  private LocalDateTime createdTime;
  /**
   * 更新时间
   */
  private LocalDateTime updatedTime;
}

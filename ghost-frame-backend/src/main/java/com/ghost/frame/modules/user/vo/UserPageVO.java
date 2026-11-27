package com.ghost.frame.modules.user.vo;

import com.ghost.frame.modules.user.enums.UserRole;
import com.ghost.frame.modules.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户分页行
 * @author 34592
 */
@Data
@Schema(description = "用户分页行")
public class UserPageVO {
  /**
   * id
   */
  @Schema(description = "id")
  private Long id;
  /**
   * 昵称
   */
  @Schema(description = "昵称")
  private String nickname;
  /**
   * 用户名
   */
  @Schema(description = "用户名")
  private String username;
  /**
   * 角色
   */
  @Schema(description = "角色")
  private UserRole  role;
  /**
   * 状态
   */
  @Schema(description = "状态")
  private UserStatus status;
  /**
   * 创建时间
   */
  @Schema(description = "创建时间")
  private LocalDateTime createdTime;
  /**
   * 更新时间
   */
  @Schema(description = "更新时间")
  private LocalDateTime updatedTime;
}

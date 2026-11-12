package com.ghost.frame.modules.user.dto;

import com.ghost.frame.modules.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户分页查询参数类
 * @author 34592
 */
@Data
@Schema(description = "用户分页查询参数类")
public class UserPageDTO {
  /**
   * 当前页码
   */
  @Schema(description = "当前页码")
  private Long pageNum = 1L;
  /**
   * 每页大小
   */
  @Schema(description = "每页大小")
  private Long pageSize = 20L;
  /**
   * 用户名
   */
  @Schema(description = "用户名")
  private String username;
  /**
   * 昵称
   */
  @Schema(description = "昵称")
  private String nickname;
  /**
   * 状态
   */
  @Schema(description = "状态")
  private UserStatus status;
}

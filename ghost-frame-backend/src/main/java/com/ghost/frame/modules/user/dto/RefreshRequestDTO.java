package com.ghost.frame.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新token请求参数，用与换新的access token
 * @author 34592
 */
@Data
@Schema(description = "刷新token请求参数")
public class RefreshRequestDTO {
  
  @NotBlank(message = "refreshToken不能为空")
  @Schema(description = "refreshToken")
  private String refreshToken;
}

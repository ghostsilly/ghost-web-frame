package com.ghost.frame.modules.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新token请求参数，用与换新的access token
 * @author 34592
 */
@Data
public class RefreshRequestDTO {
  
  @NotBlank(message = "refreshToken不能为空")
  private String refreshToken;
}

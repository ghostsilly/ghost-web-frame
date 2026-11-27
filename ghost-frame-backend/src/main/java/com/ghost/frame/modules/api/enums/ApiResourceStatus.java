package com.ghost.frame.modules.api.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * API资源状态
 * @author 34592
 */
@Getter
@Schema(description = "接口资源状态。JSON 为枚举名。EXISTS 代码中存在，REMOVED 代码中已移除")
public enum ApiResourceStatus {
  EXISTS(1, "代码中存在"),
  REMOVED(0, "代码中已移除");
  
  /**
   * 状态码
   */
  private final int code;
  /**
   * 状态描述
   */
  private final String desc;

  ApiResourceStatus(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

}

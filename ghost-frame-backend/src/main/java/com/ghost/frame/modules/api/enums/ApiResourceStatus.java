package com.ghost.frame.modules.api.enums;

import lombok.Getter;

/**
 * API资源状态
 * @author 34592
 */
@Getter
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

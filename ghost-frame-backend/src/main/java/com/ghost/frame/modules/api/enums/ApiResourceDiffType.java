package com.ghost.frame.modules.api.enums;

import lombok.Getter;

/**
 * API资源差异类型
 * @author 34592
 */
@Getter
public enum ApiResourceDiffType {
  ADDED("新增"),
  MODIFIED("修改"),
  REMOVED("删除");

  private final String desc;

  ApiResourceDiffType(String desc) {
    this.desc = desc;
  }

}

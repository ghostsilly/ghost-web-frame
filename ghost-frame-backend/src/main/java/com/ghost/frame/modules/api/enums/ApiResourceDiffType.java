package com.ghost.frame.modules.api.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * API资源差异类型
 * @author 34592
 */
@Getter
@Schema(description = "差异类型。JSON 为枚举名。ADDED 新增，MODIFIED 修改，REMOVED 删除")
public enum ApiResourceDiffType {
  ADDED("新增"),
  MODIFIED("修改"),
  REMOVED("删除");

  private final String desc;

  ApiResourceDiffType(String desc) {
    this.desc = desc;
  }

}

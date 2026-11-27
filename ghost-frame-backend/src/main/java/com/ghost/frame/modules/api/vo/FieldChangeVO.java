package com.ghost.frame.modules.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段变化VO
 * @author 34592
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字段变化VO")
public class FieldChangeVO {
  /** 
   * 字段名，如 module / summary / handler / status 
   */
  @Schema(description = "字段名")
  private String field;
  /** 
   * 表中旧值 
   */
  @Schema(description = "表中旧值")
  private String oldValue;
  /** 
   * 扫描到的新值 
   */
  @Schema(description = "扫描到的新值")
  private String newValue;
}

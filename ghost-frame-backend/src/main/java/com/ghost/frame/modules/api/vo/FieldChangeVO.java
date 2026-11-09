package com.ghost.frame.modules.api.vo;

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
public class FieldChangeVO {
  /** 
   * 字段名，如 module / summary / handler / status 
   */
  private String field;
  /** 
   * 表中旧值 
   */
  private String oldValue;
  /** 
   * 扫描到的新值 
   */
  private String newValue;
}

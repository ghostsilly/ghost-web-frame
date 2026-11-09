package com.ghost.frame.modules.api.vo;

import com.ghost.frame.modules.api.enums.ApiResourceDiffType;
import com.ghost.frame.modules.api.enums.ApiResourceStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * API资源差异项VO
 * @author 34592
 */
@Data
public class ApiResourceDiffItemVO {
  /** 
   * 差异类型 
   */
  private ApiResourceDiffType diffType;
  /** 
   * 表中已有记录的主键；ADDED 时为 null 
   */
  private Long id;
  /** 
   * 接口路径 pattern 
   */
  private String path;
  /** 
   * HTTP 方法，空串表示不限 
   */
  private String method;
  /** 
   * 所属模块（取新值） 
   */
  private String module;
  /** 
   * 接口说明（取新值） 
   */
  private String summary;
  /** 
   * 处理方法（取新值） 
   */
  private String handler;
  /** 
   * 表中现有状态；ADDED 时为 null 
   */
  private ApiResourceStatus dbStatus;
  /** 
   * 字段级变更明细；仅 MODIFIED 有值 
   */
  private List<FieldChangeVO> changes = new ArrayList<>();
}

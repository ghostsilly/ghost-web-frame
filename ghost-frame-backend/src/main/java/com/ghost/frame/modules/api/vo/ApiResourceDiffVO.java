package com.ghost.frame.modules.api.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * API资源差异VO
 * @author 34592
 */
@Data
public class ApiResourceDiffVO {
  /** 
   * 扫描时间 
   */
  private LocalDateTime scanTime;
  /** 
   * 容器路由总数 
   */
  private Integer scannedCount;
  /** 
   * 表中记录总数（未逻辑删除） 
   */
  private Integer dbCount;
  /** 
   * 新增：容器有、表里没有 
   */
  private List<ApiResourceDiffItemVO> added = new ArrayList<>();
  /** 
   * 修改：两边都有但元信息不同 
   */
  private List<ApiResourceDiffItemVO> modified = new ArrayList<>();
  /** 
   * 删除：表里有、容器已无 
   */
  private List<ApiResourceDiffItemVO> removed = new ArrayList<>();
  /** 
   * 完全一致、无需处理的数量 
   */
  private Integer unchangedCount = 0;
}
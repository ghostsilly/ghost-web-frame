package com.ghost.frame.modules.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API资源申请结果VO
 * @author 34592
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResourceApplyResultVO {

  /**
   * 成功新增的接口数
   */
  private Integer inserted;

  /**
   * 成功更新的接口数
   */
  private Integer updated;

  /**
   * 成功标记为「代码中已移除」的接口数
   */
  private Integer removed;

  /**
   * 跳过的条数：新增时库里已存在，或更新时 id 已不存在
   */
  private Integer skipped;
}

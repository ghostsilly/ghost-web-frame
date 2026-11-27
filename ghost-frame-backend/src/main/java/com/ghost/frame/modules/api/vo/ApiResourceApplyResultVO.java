package com.ghost.frame.modules.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "API资源申请结果VO")
public class ApiResourceApplyResultVO {

  /**
   * 成功新增的接口数
   */
  @Schema(description = "成功新增的接口数")
  private Integer inserted;

  /**
   * 成功更新的接口数
   */
  @Schema(description = "成功更新的接口数")
  private Integer updated;

  /**
   * 成功标记为「代码中已移除」的接口数
   */
  @Schema(description = "成功标记为「代码中已移除」的接口数")
  private Integer removed;

  /**
   * 跳过的条数：新增时库里已存在，或更新时 id 已不存在
   */
  @Schema(description = "跳过的条数：新增时库里已存在，或更新时 id 已不存在")
  private Integer skipped;
}

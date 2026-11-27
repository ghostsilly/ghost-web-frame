package com.ghost.frame.modules.api.dto;

import com.ghost.frame.modules.api.entity.ApiResource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * API资源申请DTO
 * @author 34592
 */
@Data
@Schema(description = "API资源申请DTO")
public class ApiResourceApplyDTO {
  /** 
   * 确认新增的接口，来自 diff.added 
   */
  @Valid
  @Schema(description = "确认新增的接口")
  private List<ApiResource> inserts = new ArrayList<>();

  /** 
   * 确认更新的接口，必须带 id，来自 diff.modified 
   */
  @Valid
  @Schema(description = "确认更新的接口")
  private List<ApiResource> updates = new ArrayList<>();

  /** 
   * 确认标记为「代码中已移除」的主键，来自 diff.removed 
   */
  @Schema(description = "确认标记为「代码中已移除」的主键")
  private List<Long> removeIds = new ArrayList<>();
}

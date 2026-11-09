package com.ghost.frame.modules.api.dto;

import com.ghost.frame.modules.api.entity.ApiResource;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * API资源申请DTO
 * @author 34592
 */
@Data
public class ApiResourceApplyDTO {
  /** 
   * 确认新增的接口，来自 diff.added 
   */
  @Valid
  private List<ApiResource> inserts = new ArrayList<>();

  /** 
   * 确认更新的接口，必须带 id，来自 diff.modified 
   */
  @Valid
  private List<ApiResource> updates = new ArrayList<>();

  /** 
   * 确认标记为「代码中已移除」的主键，来自 diff.removed 
   */
  private List<Long> removeIds = new ArrayList<>();
}

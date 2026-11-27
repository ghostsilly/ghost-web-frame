package com.ghost.frame.modules.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ghost.frame.modules.common.entity.BaseEntity;
import com.ghost.frame.modules.api.enums.ApiResourceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 后端 api 接口资源类
 * @author 34592
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "api_resource")
@Schema(description = "接口资源。path 是路径 pattern，含 BaseEntity 公共字段")
public class ApiResource extends BaseEntity {
  /**
   * 主键
   */
  @Schema(description = "主键。新增项可不传，更新项必须带 id")
  private Long id;
  /**
   * 路径
   */
  @Schema(description = "路径 pattern")
  private String path;
  /**
   * 方法
   */
  @Schema(description = "HTTP 方法，空串表示不限")
  private String method;
  /**
   * 模块
   */
  @Schema(description = "模块，取控制器 @Tag.name")
  private String module;
  /**
   * 简要描述
   */
  @Schema(description = "简要描述，取方法 @Operation.summary")
  private String summary;
  /**
   * 处理类
   */
  @Schema(description = "处理类")
  private String handler;
  /**
   * 状态
   */
  @Schema(description = "状态")
  private ApiResourceStatus status;
}

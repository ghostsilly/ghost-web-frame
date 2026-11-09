package com.ghost.frame.modules.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ghost.frame.modules.common.entity.BaseEntity;
import com.ghost.frame.modules.api.enums.ApiResourceStatus;
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
public class ApiResource extends BaseEntity {
  /**
   * 主键
   */
  private Long id;
  /**
   * 路径
   */
  private String path;
  /**
   * 方法
   */
  private String method;
  /**
   * 模块
   */
  private String module;
  /**
   * 简要描述
   */
  private String summary;
  /**
   * 处理类
   */
  private String handler;
  /**
   * 状态
   */
  private ApiResourceStatus status;
}

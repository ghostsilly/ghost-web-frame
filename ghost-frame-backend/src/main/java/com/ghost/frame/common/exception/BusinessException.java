package com.ghost.frame.common.exception;

import com.ghost.frame.common.enums.ErrorCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException{
  private final Integer code;

  /**
   * 构造方法
   * @param code 错误码
   */
  public BusinessException(ErrorCode code) {
    super(code.getMessage());
    this.code =code.getCode();
  }
}

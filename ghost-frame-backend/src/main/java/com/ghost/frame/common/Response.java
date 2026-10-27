
package com.ghost.frame.common;

import com.ghost.frame.common.throwable.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 响应结果封装类
 * @author 34592
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
  /**
   * 响应码
   */
  private  Integer code;

  /**
   * 响应信息
   */
  private String message;

  /**
   * 响应数据
   */
  private T data;

  public static <T> Response<T> success(T data) {
    return new Response<>(200, "运行成功", data);
  }

  public static <T> Response<T> success() {
    return new Response<>(200, "运行成功", null);
  }

  public static <T> Response<T> error(Integer code, String message) {
    return new Response<>(code, message, null);
  }

  public static <T> Response<T> error(String message) {
    return new Response<>(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
  }


}

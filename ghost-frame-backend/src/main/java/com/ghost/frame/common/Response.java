
package com.ghost.frame.common;

import lombok.Data;

@Data
public class Response<T> {
  /**
   * 响应码
   */
  private String code;

  /**
   * 响应信息
   */
  private String message;

  /**
   * 响应数据
   */
  private T data;

  public static <T> Response<T> success(T data) {
    Response<T> response = new Response<>();
    response.setCode("200");
    response.setMessage("成功");
    response.setData(data);
    return response;
  }

}


package com.ghost.frame.modules.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 34592
 * @description 基础控制器
 */
@RestController
public class BaseController {
  
  /**
   * 获取当前用户id
   * @return 当前用户id
   */
  public Long getUserId(){
    return Long.valueOf(
        SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal()
            .toString()
    );
  }

}

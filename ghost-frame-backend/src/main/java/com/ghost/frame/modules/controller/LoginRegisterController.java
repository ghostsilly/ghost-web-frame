package com.ghost.frame.modules.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api}/auth")
@Tag(name = "用户模块", description = "用户的CRUD接口")
public class LoginRegisterController {


  @Operation(summary = "用户登录")
  @PostMapping("/login")
  public String login() {
    // todo: 登录逻辑
    return "login";
  }
}

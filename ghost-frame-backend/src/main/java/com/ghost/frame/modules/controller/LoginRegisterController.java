package com.ghost.frame.modules.controller;

import com.ghost.frame.common.Response;
import com.ghost.frame.modules.dto.LoginRequestDTO;
import com.ghost.frame.modules.dto.RegisterRequestDTO;
import com.ghost.frame.modules.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api}/auth")
@Tag(name = "用户模块", description = "用户的CRUD接口")
public class LoginRegisterController {
  
  @Autowired
  private UserService userService;


  @Operation(summary = "用户登录")
  @PostMapping("/login")
  public Response<String> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    // todo: 登录逻辑
    return Response.success(userService.login(loginRequestDTO));
  }
  
  @Operation(summary = "用户注册")
  @PostMapping("/register")
  public Response<String> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
    // todo: 注册逻辑
    return Response.success(userService.register(registerRequestDTO));
  }
}

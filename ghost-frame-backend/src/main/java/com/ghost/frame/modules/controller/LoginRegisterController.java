package com.ghost.frame.modules.controller;

import com.ghost.frame.common.Response;
import com.ghost.frame.modules.dto.LoginRequestDTO;
import com.ghost.frame.modules.dto.RegisterRequestDTO;
import com.ghost.frame.modules.service.UserService;
import com.ghost.frame.modules.vo.LoginRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 34592
 * @description 用户模块
 */
@RestController
@RequestMapping("${api}/auth")
@Tag(name = "用户模块", description = "用户的CRUD接口")
public class LoginRegisterController extends BaseController{
  
  @Autowired
  private UserService userService;


  @Operation(summary = "用户登录")
  @PostMapping("/login")
  public Response<LoginRequestVO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
    return Response.success(userService.login(loginRequestDTO));
  }
  
  @Operation(summary = "用户注册")
  @PostMapping("/register")
  public Response<LoginRequestVO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
    return Response.success(userService.register(registerRequestDTO));
  }
  
  @Operation(summary = "获取当前用户")
  @GetMapping("/me")
  public Response<LoginRequestVO> obtainTheCurrentUser() {
    Long userId = getUserId();
    return Response.success(userService.obtainTheCurrentUser(userId));
  }
  
}

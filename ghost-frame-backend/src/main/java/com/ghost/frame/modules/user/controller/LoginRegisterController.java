package com.ghost.frame.modules.user.controller;

import com.ghost.frame.modules.common.entity.Response;
import com.ghost.frame.modules.common.controller.BaseController;
import com.ghost.frame.modules.user.dto.LoginRequestDTO;
import com.ghost.frame.modules.user.dto.RefreshRequestDTO;
import com.ghost.frame.modules.user.dto.RegisterRequestDTO;
import com.ghost.frame.modules.user.service.UserService;
import com.ghost.frame.modules.user.vo.LoginRequestVO;
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
@Tag(name = "登录模块", description = "登录注册认证映射")
public class LoginRegisterController extends BaseController {
  
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
  
  @Operation(summary = "获取当前用户信息")
  @GetMapping("/me")
  public Response<LoginRequestVO> obtainTheCurrentUser() {
    Long userId = getUserId();
    return Response.success(userService.obtainTheCurrentUser(userId));
  } 
  
  @Operation(summary = "用户登出")
  @PostMapping("/logout")
  public Response<String> logout() {
    getUserId();
    return Response.success();
  }
  
  @Operation(summary = "用户token刷新")
  @PostMapping("/refresh")
  public Response<LoginRequestVO> refresh(@Valid @RequestBody RefreshRequestDTO refreshRequestDTO){
    return Response.success(userService.refresh(refreshRequestDTO));
  }
  
}

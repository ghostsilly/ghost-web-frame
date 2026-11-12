package com.ghost.frame.modules.user.controller;

import com.ghost.frame.modules.common.controller.BaseController;
import com.ghost.frame.modules.common.entity.PageResult;
import com.ghost.frame.modules.common.entity.Response;
import com.ghost.frame.modules.user.dto.UserPageDTO;
import com.ghost.frame.modules.user.service.UserService;
import com.ghost.frame.modules.user.vo.UserPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * @author 34592
 */
@RestController
@RequestMapping("${api}/user")
@Tag(name = "用户模块", description = "用户模块的CRUD操作")
@RequiredArgsConstructor
@Slf4j
public class UserController extends BaseController {
  
  private final UserService userService;
  @Operation(summary = "获取用户列表")
  @GetMapping("/page")
  public Response<PageResult<UserPageVO>> pageUsers(UserPageDTO dto){
    log.info("分页查询用户列表，参数：{}", dto);
    return Response.success(userService.pageUsers(dto));
  }
  
}

package com.ghost.frame.modules.user.service;

import com.ghost.frame.modules.user.dto.LoginRequestDTO;
import com.ghost.frame.modules.user.dto.RefreshRequestDTO;
import com.ghost.frame.modules.user.dto.RegisterRequestDTO;
import com.ghost.frame.modules.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ghost.frame.modules.user.vo.LoginRequestVO;
import jakarta.validation.Valid;

/**
* @author 34592
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-08-09 11:07:24
*/
public interface UserService extends IService<User> {

  LoginRequestVO login(@Valid LoginRequestDTO loginRequestDTO);

  LoginRequestVO register(@Valid RegisterRequestDTO registerRequestDTO);

  LoginRequestVO obtainTheCurrentUser(Long userId);
  
  LoginRequestVO refresh(@Valid RefreshRequestDTO refreshRequestDTO);
}

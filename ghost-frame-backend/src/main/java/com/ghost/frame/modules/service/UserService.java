package com.ghost.frame.modules.service;

import com.ghost.frame.modules.dto.LoginRequestDTO;
import com.ghost.frame.modules.dto.RegisterRequestDTO;
import com.ghost.frame.modules.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;

/**
* @author 34592
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-08-09 11:07:24
*/
public interface UserService extends IService<User> {

  String login(@Valid LoginRequestDTO loginRequestDTO);

  String register(@Valid RegisterRequestDTO registerRequestDTO);
}

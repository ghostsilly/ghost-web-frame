package com.ghost.frame.modules.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.frame.common.enums.ErrorCode;
import com.ghost.frame.common.exception.BusinessException;
import com.ghost.frame.modules.dto.LoginRequestDTO;
import com.ghost.frame.modules.dto.RefreshRequestDTO;
import com.ghost.frame.modules.dto.RegisterRequestDTO;
import com.ghost.frame.modules.entity.User;
import com.ghost.frame.modules.enums.DeleteFlag;
import com.ghost.frame.modules.enums.UserRole;
import com.ghost.frame.modules.enums.UserStatus;
import com.ghost.frame.modules.service.UserService;
import com.ghost.frame.modules.mapper.UserMapper;
import com.ghost.frame.modules.vo.LoginRequestVO;
import com.ghost.frame.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
* @author 34592
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-08-09 11:07:24
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public LoginRequestVO login(LoginRequestDTO loginRequestDTO) {
    User user = userMapper.selectOne(
        new LambdaQueryWrapper<User>()
            .eq(User::getUsername, loginRequestDTO.getUsername())
            .eq(User::getDeleted, DeleteFlag.NOT_DELETED)
    );
    
    if (ObjectUtil.isAllEmpty(user) || !passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())){
      throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
    }
    if (user.getStatus().equals(UserStatus.LOCKED)){
      throw new BusinessException(ErrorCode.USER_DISABLED);
    }
    return new LoginRequestVO(
        user.getId(), 
        user.getNickname(), 
        user.getUsername(), 
        user.getRole(), 
        jwtUtil.generateAccessToken(user.getId(), user.getUsername()),
        jwtUtil.generateRefreshToken(user.getId(), user.getUsername())
    );
  }

  @Override
  public LoginRequestVO register(RegisterRequestDTO registerRequestDTO) {
    User userOne = userMapper.selectOne(
        new LambdaQueryWrapper<User>().eq(User::getUsername, registerRequestDTO.getUsername())
    );

    User user = new User();

    Long l = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getNickname, registerRequestDTO.getNickname()));

    if (l > 0){
      throw new BusinessException(ErrorCode.NICKNAME_EXISTS);
    }

    if (ObjectUtil.isNotEmpty(userOne)){
      if (userOne.getDeleted().equals(DeleteFlag.NOT_DELETED)){
        throw new BusinessException(ErrorCode.USERNAME_EXISTS);
      }
      
      user.setId(userOne.getId());
      user.setNickname(registerRequestDTO.getNickname());
      user.setUsername(registerRequestDTO.getUsername());
      user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
      user.setRole(UserRole.USER);
      user.setStatus(UserStatus.NORMAL);
      user.setDeleted(DeleteFlag.NOT_DELETED);
      
      userMapper.update(
          user, new LambdaQueryWrapper<User>().eq(User::getId, userOne.getId())
      );

      return new LoginRequestVO(
          user.getId(),
          user.getNickname(),
          user.getUsername(),
          user.getRole(),
          jwtUtil.generateAccessToken(user.getId(), user.getUsername()),
          jwtUtil.generateRefreshToken(user.getId(), user.getUsername())
      );
    }
    
    user.setNickname(registerRequestDTO.getNickname());
    user.setUsername(registerRequestDTO.getUsername());
    user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
    user.setRole(UserRole.USER);
    user.setStatus(UserStatus.NORMAL);
    user.setDeleted(DeleteFlag.NOT_DELETED);
    
    userMapper.insert(user);

    return new LoginRequestVO(
        user.getId(),
        user.getNickname(),
        user.getUsername(),
        user.getRole(),
        jwtUtil.generateAccessToken(user.getId(), user.getUsername()),
        jwtUtil.generateRefreshToken(user.getId(), user.getUsername())
    );
  }

  @Override
  public LoginRequestVO obtainTheCurrentUser(Long userId) {
    return userMapper.getUserInformation(userId);
  }

  @Override
  public LoginRequestVO refresh(RefreshRequestDTO refreshRequestDTO) {
    if (!jwtUtil.isRefreshToken(refreshRequestDTO.getRefreshToken())){
      throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }
    Long userId = jwtUtil.getUserId(refreshRequestDTO.getRefreshToken());
    User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
        .eq(User::getId, userId)
        .eq(User::getDeleted, DeleteFlag.NOT_DELETED)
    );
    if (ObjectUtil.isEmpty(user)){
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
    if (user.getStatus().equals(UserStatus.LOCKED)){
      throw new BusinessException(ErrorCode.USER_DISABLED);
    }
    return new LoginRequestVO(
        user.getId(),
        user.getNickname(),
        user.getUsername(),
        user.getRole(),
        jwtUtil.generateAccessToken(user.getId(), user.getUsername()),
        refreshRequestDTO.getRefreshToken()
    );
  }
}





package com.ghost.frame.modules;

import com.ghost.frame.common.throwable.exception.BusinessException;
import com.ghost.frame.modules.dto.LoginRequestDTO;
import com.ghost.frame.modules.dto.RegisterRequestDTO;
import com.ghost.frame.modules.entity.User;
import com.ghost.frame.modules.enums.DeleteFlag;
import com.ghost.frame.modules.enums.UserRole;
import com.ghost.frame.modules.enums.UserStatus;
import com.ghost.frame.modules.mapper.UserMapper;
import com.ghost.frame.modules.service.impl.UserServiceImpl;
import com.ghost.frame.modules.vo.LoginRequestVO;
import com.ghost.frame.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  
  @Mock
  private UserMapper userMapper;
  
  @Mock
  private PasswordEncoder passwordEncoder;
  
  @Mock
  private JwtUtil jwtUtil;
  
  @InjectMocks
  private UserServiceImpl userService;
  
  /**
   * 注册用户名重复测试
   */
  @Test
  void registerUsernameRepetition() {
    User user = new User();
    user.setDeleted(DeleteFlag.NOT_DELETED);
    when(userMapper.selectOne(any())).thenReturn(user);
    when(userMapper.selectCount(any())).thenReturn(0L);
    RegisterRequestDTO requestDTO = new RegisterRequestDTO(
        "kuangxinkai", 
        "1234", 
        "ghost_silly"
    );

    BusinessException businessException = assertThrows(
        BusinessException.class, 
        () -> userService.register(requestDTO)
    );
    assertEquals(400, businessException.getCode());
    verify(userMapper, never()).insert(any(User.class));
  }

  /**
   * 登录密码错误
   */
  @Test
  void loginPasswordError() {
    User user = new User();
    user.setId(1L);
    user.setUsername("kuangxinkai");
    user.setPassword("hashed");
    user.setStatus(UserStatus.NORMAL);
    when(userMapper.selectOne(any())).thenReturn(user);
    when(passwordEncoder.matches("secret-1", "hashed")).thenReturn(false);

    LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
        "kuangxinkai",
        "secret-1"
    );

    BusinessException businessException = assertThrows(
        BusinessException.class,
        () -> userService.login(loginRequestDTO)
    );
    
    assertEquals(400, businessException.getCode());
    verify(jwtUtil, never()).generateAccessToken(any(), any());
  }

  /**
   * 登录成功
   */
  @Test
  void loginSuccess() {
    User user = new User();
    user.setId(1L);
    user.setUsername("kuangxinkai");
    user.setPassword("hashed");
    user.setRole(UserRole.USER);
    user.setStatus(UserStatus.NORMAL);
    when(userMapper.selectOne(any())).thenReturn(user);
    when(passwordEncoder.matches("secret-1", "hashed")).thenReturn(true);
    when(jwtUtil.generateAccessToken(1L, "kuangxinkai")).thenReturn("access_token");
    when(jwtUtil.generateRefreshToken(1L, "kuangxinkai")).thenReturn("refresh_token");
    
    LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
        "kuangxinkai",
        "secret-1"
    );

    LoginRequestVO login = userService.login(loginRequestDTO);
    assertEquals("kuangxinkai", login.getUsername());
    assertEquals("access_token", login.getToken());
    assertEquals("refresh_token", login.getRefreshToken());

  }
  
}

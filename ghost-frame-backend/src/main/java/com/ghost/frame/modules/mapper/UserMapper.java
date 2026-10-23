package com.ghost.frame.modules.mapper;

import com.ghost.frame.modules.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.frame.modules.vo.LoginRequestVO;

/**
* @author 34592
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2026-08-09 11:07:24
* @Entity com.ghost.frame.modules/entity.User
*/
public interface UserMapper extends BaseMapper<User> {

  LoginRequestVO getUserInformation(Long userId);
}





package com.ghost.frame.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.frame.modules.entity.User;
import com.ghost.frame.modules.service.UserService;
import com.ghost.frame.modules.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 34592
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-08-09 11:07:24
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}





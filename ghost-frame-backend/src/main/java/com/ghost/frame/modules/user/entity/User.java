package com.ghost.frame.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;

import com.ghost.frame.modules.common.entity.BaseEntity;
import com.ghost.frame.modules.user.enums.UserRole;
import com.ghost.frame.modules.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author 34592
 * &#064;TableName   user 用户表  
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value ="user")
@Data
public class User extends BaseEntity {
    /**
     * 主键
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色
     * USER-普通用户    ADMIN-管理员   SUPER_ADMIN-超级管理员
     */
    private UserRole role;

    /**
     * 状态
     *1-正常  0-禁用
     */
    private UserStatus status;
    
}
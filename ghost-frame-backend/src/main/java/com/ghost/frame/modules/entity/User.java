package com.ghost.frame.modules.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

import com.ghost.frame.modules.enums.DeleteFlag;
import com.ghost.frame.modules.enums.UserRole;
import com.ghost.frame.modules.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @TableName user
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="user")
@Data
public class User extends BaseEntity{
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

    /**
     * 删除状态
     * 0-未删除  1-已删除
     */
    private DeleteFlag deleted;

}
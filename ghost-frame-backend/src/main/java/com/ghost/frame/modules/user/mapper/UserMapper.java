package com.ghost.frame.modules.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.frame.modules.user.dto.UserPageDTO;
import com.ghost.frame.modules.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.frame.modules.user.vo.LoginRequestVO;
import com.ghost.frame.modules.user.vo.UserPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 34592
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2026-08-09 11:07:24
* @Entity com.ghost.frame.modules/entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

  LoginRequestVO getUserInformation(Long userId);

  Page<UserPageVO> selectUserPage(Page<UserPageVO> userPageVOPage,@Param("dto") UserPageDTO dto);
}





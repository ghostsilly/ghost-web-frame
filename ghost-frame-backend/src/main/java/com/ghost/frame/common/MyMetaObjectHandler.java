package com.ghost.frame.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充：创建/更新时间与操作人
 * 自定义元数据处理器
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long operatorId = getCurrentUserId();
        // 创建字段（仅 INSERT）
        strictInsertFill(metaObject, "createdDate", LocalDateTime.class, now);
        strictInsertFill(metaObject, "createdBy", Long.class, operatorId);
        // 更新字段（INSERT 时也要填一次初始值）
        strictInsertFill(metaObject, "updatedDate", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updatedBy", Long.class, operatorId);
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long operatorId = getCurrentUserId();
        strictUpdateFill(metaObject, "updatedDate", LocalDateTime.class, now);
        strictUpdateFill(metaObject, "updatedBy", Long.class, operatorId);
    }
    /**
     * 获取当前登录用户 ID。
     * JWT 接入前可返回 null；接入后从 SecurityContext 读取。
     */
    private Long getCurrentUserId() {
        // TODO: 阶段 C 鉴权完成后改为从 SecurityContext 获取
        return null;
    }
}

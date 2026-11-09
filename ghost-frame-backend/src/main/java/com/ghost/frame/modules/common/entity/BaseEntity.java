package com.ghost.frame.modules.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ghost.frame.modules.common.enums.DeleteFlag;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ghost
 * @description 实体类基类
 */
@Data
public class BaseEntity {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedDate;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    /**
     * 删除状态
     * 0-未删除  1-已删除
     */
    private DeleteFlag deleted;

    /**
     * 备注
     */
    private String remark;
}

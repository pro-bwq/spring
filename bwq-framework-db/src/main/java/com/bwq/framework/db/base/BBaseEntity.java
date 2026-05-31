package com.bwq.framework.db.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author bwq
 * @date 2026-04-16 11:11:48
 * @description 实体基础类
 */

@Data
public abstract class BBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     * */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间
     * */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标志：0正常，1已删除
     * */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @Version
    private Integer version;
}

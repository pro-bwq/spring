package com.bwq.framework.db.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author bwq
 * @date 2026-05-28 09:04:48
 * @description 基础Service接口
 */

public interface BBaseService<T> extends IService<T>{

    /**
     * 根据ID软删除
     */
    boolean logicDelete(Long id);

    /**
     * 根据条件软删除
     */
    boolean logicDelete(QueryWrapper<T> queryWrapper);
}

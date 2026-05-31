package com.bwq.framework.db.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author bwq
 * @date 2026-05-29 00:07:42
 * @description 基础Service实现类
 *  @param <M> Mapper类型
 *  @param <T> 实体类型
 */
@Service
public abstract class BBaseServiceImpl<M extends BBaseMapper<T>, T extends BBaseEntity>
        extends ServiceImpl<M, T> implements BBaseService<T> {

    @Override
    public boolean logicDelete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean logicDelete(QueryWrapper<T> queryWrapper) {
        return remove(queryWrapper);
    }
}

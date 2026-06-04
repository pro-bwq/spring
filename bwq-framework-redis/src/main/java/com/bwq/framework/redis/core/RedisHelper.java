package com.bwq.framework.redis.core;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author bwq
 * @date 2026-05-28 21:33:57
 * @description 整合 String、Hash、List、Set、ZSet 以及分布式锁功能
 */

@Component
@RequiredArgsConstructor
public class RedisHelper {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    // ==================== String 操作 ====================

    /**
     * 设置值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    /**
     * 设置值并指定过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    /**
     * 设置值（过期时间单位：秒）
     */
    public void set(String key, Object value, long timeoutSeconds) {
        set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 获取值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取值（带类型转换）
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        // 这里可以加类型转换逻辑，如果已经是目标类型直接返回
        return clazz.isInstance(value) ? clazz.cast(value) : null;
    }

    /**
     * 删除键
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除
     */
    public Long delete(String... keys) {
        return redisTemplate.delete(List.of(keys));
    }

    /**
     * 判断键是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 自增（返回自增后的值）
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增指定步长（指定的数）
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 仅当 key 不存在时设置值（分布式锁基础方法）
     *
     * @param key 缓存 key
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true 设置成功（获取锁成功），false key 已存在（获取锁失败）
     */
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(key, value, timeout, unit);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 仅当 key 不存在时设置值（无过期时间）
     */
    public boolean setIfAbsent(String key, Object value) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
        return Boolean.TRUE.equals(result);
    }


    // ==================== Hash 操作 ====================

    /**
     * 设置 Hash 字段
     */
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 批量设置 Hash 字段【批量往抽屉的小格子里放东西】
     */
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取 Hash 字段
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取所有 Hash 字段
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取所有 Hash 字段（带类型转换）
     */
    @SuppressWarnings("unchecked")
    public <T> T hGetAll(String key, Class<T> clazz) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        // 这里需要实现 Map 到对象的转换，可以使用 Jackson
        // 简化处理：返回 Map 对象，由调用方自行转换
        return (T) entries;
    }

    /**
     * 获取多个 Hash 字段
     */
    public List<Object> hMultiGet(String key, Collection<String> hashKeys) {
        List<Object> keys = new ArrayList<>(Arrays.asList(hashKeys));
        return redisTemplate.opsForHash().multiGet(key, keys);
    }

    /**
     * 删除 Hash 字段
     */
    public Long hDelete(String key, String... hashKeys) {
        return redisTemplate.opsForHash().delete(key, (Object[]) hashKeys);
    }

    /**
     * 判断 Hash 字段是否存在
     */
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Hash 字段自增
     */
    public Long hIncrement(String key, String hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 获取 Hash 所有值
     */
    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    // ==================== List 操作 ====================

    /**
     * 左侧插入
     */
    public Long lLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 右侧插入
     */
    public Long lRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 批量右侧插入
     */
    public Long lRightPushAll(String key, Collection<?> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 左侧弹出
     */
    @SuppressWarnings("unchecked")
    public <T> T lLeftPop(String key) {
        return (T) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 右侧弹出
     */
    @SuppressWarnings("unchecked")
    public <T> T lRightPop(String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取 List 全部元素
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> lRangeAll(String key) {
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            return List.of();
        }
        return (List<T>) redisTemplate.opsForList().range(key, 0, size - 1);
    }

    /**
     * 获取 List 指定范围
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> lRange(String key, long start, long end) {
        return (List<T>) redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取 List 长度
     */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // ==================== Set 操作 ====================

    /**
     * 添加 Set 元素
     */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取 Set 所有元素
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> sMembers(String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断是否是 Set 成员
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 删除 Set 元素
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取 Set 大小
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    // ==================== ZSet 操作 ====================

    /**
     * 添加 ZSet 元素（带分数）
     */
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取 ZSet 分数
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 获取 ZSet 排名（升序，从0开始）
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取 ZSet 排名（降序）
     */
    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取 ZSet 指定分数范围的元素
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> zRangeByScore(String key, double min, double max) {
        return (Set<T>) redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取 ZSet 指定排名范围的元素
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> zRange(String key, long start, long end) {
        return (Set<T>) redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取 ZSet 指定排名范围的元素（降序）
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> zReverseRange(String key, long start, long end) {
        return (Set<T>) redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 增加 ZSet 元素分数
     */
    public Double zIncrementScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 获取 ZSet 大小
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    // ==================== 分布式锁（Redisson）====================

    /**
     * 获取分布式锁（需手动释放）
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 加锁（带自动释放）
     */
    public void lock(String lockKey, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 加锁（带自动释放，支持返回值）
     */
    public <T> T lock(String lockKey, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试加锁（带超时）
     *
     * @param lockKey 锁的key
     * @param waitTime 等待获取锁的最长时间
     * @param unit 时间单位
     * @param runnable 业务逻辑（无返回值）
     * @return boolean(true/false)
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(waitTime, leaseTime, unit)) {
                try {
                    runnable.run();
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    /**
     * 尝试加锁（自动续期）
     *
     * @param lockKey 锁的key
     * @param waitTime 等待获取锁的最长时间
     * @param unit 时间单位
     * @param runnable 业务逻辑（无返回值）
     * @return boolean(true/false)
     */
    public boolean tryLock(String lockKey, long waitTime, TimeUnit unit, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(waitTime, unit)) {
                try {
                    runnable.run();
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    /**
     * 尝试加锁（自动续期）
     *
     * @param lockKey 锁的key
     * @param waitTime 等待获取锁的最长时间
     * @param unit 时间单位
     * @param supplier 业务逻辑（有返回值）
     * @return 业务执行结果，获取锁失败返回 null
     */
    public <T> T tryLock(String lockKey, long waitTime, TimeUnit unit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 不传 leaseTime，Redisson 会自动续期
            if (lock.tryLock(waitTime, unit)) {
                try {
                    return supplier.get();
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * 尝试加锁（指定锁持有时间，不自动续期）
     * 注意：确保 leaseTime 大于任务执行时间，否则锁会提前释放
     *
     * @param lockKey 锁的key
     * @param waitTime 等待获取锁的最长时间
     * @param leaseTime 锁持有时间（时间到了自动释放，不续期）
     * @param unit 时间单位
     * @param supplier 业务逻辑（有返回值）
     * @return 业务执行结果，获取锁失败返回 null
     */
    public <T> T tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(waitTime, leaseTime, unit)) {
                try {
                    return supplier.get();
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }



    // ==================== 通用操作 ====================

    /**
     * 获取值的类型
     */
    public String getType(String key) {
        return redisTemplate.type(key).code();
    }

    /**
     * 获取 RedisTemplate（用于高级自定义操作）
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 获取 RedissonClient（用于高级分布式操作）
     */
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }


}

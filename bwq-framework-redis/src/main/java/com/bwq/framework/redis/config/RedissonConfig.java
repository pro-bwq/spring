package com.bwq.framework.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author bwq
 * @date 2026-05-31 16:52:13
 * @description TODO
 */

@Configuration
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedissonConfig {

    /**
     * Redisson 客户端（用于分布式锁等高级功能）
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        // 单机模式
        if (redisProperties.getHost() != null) {
            String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
            config.useSingleServer()
                    .setAddress(address)
                    .setPassword(redisProperties.getPassword())
                    .setDatabase(redisProperties.getDatabase())
                    .setConnectionPoolSize(10)
                    .setConnectionMinimumIdleSize(2);
        }
        // TODO: 支持集群、哨兵模式

        return Redisson.create(config);
    }

}

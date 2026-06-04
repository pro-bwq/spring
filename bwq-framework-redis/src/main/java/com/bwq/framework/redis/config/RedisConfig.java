package com.bwq.framework.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author bwq
 * @date 2026-05-28 11:17:20
 * @description Redis 自动配置类
 */

@Configuration
@ConditionalOnClass(RedisTemplate.class) // 必须引入了 spring-boot-starter-data-redis 依赖
public class RedisConfig {

    /**
     * 自定义 RedisTemplate，使用 Jackson 序列化
     *
     * @param factory Redis 连接工厂
     * @return RedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置 Key 序列化器（使用 String 序列化）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 配置 Jackson 序列化器
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = createJacksonSerializer();
        // 设置 Value 序列化器（使用 Jackson）
        template.setValueSerializer(jacksonSerializer);
        template.setHashValueSerializer(jacksonSerializer);

        // 启用默认的类型转换器
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 创建 Jackson 序列化器，解决泛型丢失和日期时间问题
     */
    private Jackson2JsonRedisSerializer<Object> createJacksonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. 注册 Java 8 时间模块（支持 LocalDate、LocalDateTime 等）
        objectMapper.registerModule(new JavaTimeModule());

        // 2. 设置可见性（只序列化 public 字段和 getter），先否定再肯定的策略
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY);

        // 3. 启用类型信息（解决泛型丢失问题）
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }
}

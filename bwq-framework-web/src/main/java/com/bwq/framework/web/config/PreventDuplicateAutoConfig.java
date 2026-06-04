package com.bwq.framework.web.config;

import com.bwq.framework.redis.core.RedisHelper;
import com.bwq.framework.web.aspect.PreventDuplicateAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author bwq
 * @date 2026-06-04 18:26:25
 * @description 自动配置防止重复提交AOP
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisHelper.class)
@EnableAspectJAutoProxy
public class PreventDuplicateAutoConfiguration {

    @Bean
    public PreventDuplicateAspect preventDuplicateAspect(RedisHelper redisHelper) {
        return new PreventDuplicateAspect(redisHelper);
    }
}
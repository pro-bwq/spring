package com.bwq.framework.common.config;

import com.bwq.framework.common.user.UserContextInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bwq
 * @date 2026-05-30 01:29:19
 * @description 注册用户上下文拦截器
 */

@Configuration
@ConditionalOnWebApplication
public class WebAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public UserContextInterceptor userContextInterceptor() {
        return new UserContextInterceptor();
    }
}

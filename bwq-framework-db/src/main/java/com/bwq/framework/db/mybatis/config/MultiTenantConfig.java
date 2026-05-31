package com.bwq.framework.db.mybatis.config;

import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.bwq.framework.db.mybatis.handler.MultiTenantHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bwq
 * @date 2026-05-30 00:21:58
 * @description 多租户配置类
 */

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "bwq.db.tenant", name = "enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class MultiTenantConfig {

    private final MultiTenantHandler multiTenantHandler;

    /**
     * 多租户拦截器（注入到 MyBatis-Plus 插件链中）
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
        log.info("初始化多租户拦截器");
        return new TenantLineInnerInterceptor(multiTenantHandler);
    }
}

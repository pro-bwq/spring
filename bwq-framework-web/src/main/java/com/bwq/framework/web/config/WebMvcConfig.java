package com.bwq.framework.web.config;

import com.bwq.framework.web.interceptor.UserContextInterceptor;
import com.bwq.framework.web.interceptor.TokenParseInterceptor;
import com.bwq.framework.web.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author bwq
 * @date 2026-05-31 12:15:43
 * @description 跨域、拦截器注册
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenParseInterceptor tokenParseInterceptor;
    private final UserContextInterceptor userContextInterceptor;

    @Bean
    @ConditionalOnMissingBean
    public TokenParseInterceptor tokenParseInterceptor(JwtProperties jwtProperties) {
        return new TokenParseInterceptor(jwtProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserContextInterceptor userContextInterceptor() {
        return new UserContextInterceptor();
    }

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        log.debug("跨域配置已启用");
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Token 解析拦截器（优先级高，先执行）- 仅在启用时生效
        if (tokenParseInterceptor != null){
            registry.addInterceptor(tokenParseInterceptor)
                    .addPathPatterns("/**")
                    .order(1);
        }

        // 用户上下文拦截器（优先级低，后执行）
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/actuator/**"
                );
        log.debug("用户上下文拦截器已注册");
        log.info("Token 解析拦截器已注册（启用状态: {}）", tokenParseInterceptor != null);
    }
}


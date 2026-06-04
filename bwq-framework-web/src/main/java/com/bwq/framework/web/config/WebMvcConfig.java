package com.bwq.framework.web.config;

import com.bwq.framework.web.interceptor.UserContextInterceptor;
import com.bwq.framework.web.interceptor.TokenParseInterceptor;
import com.bwq.framework.web.properties.JwtProperties;
import com.bwq.framework.web.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author bwq
 * @date 2026-05-31 12:15:43
 * @description 跨域、拦截器注册
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({JwtProperties.class, SecurityProperties.class})
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtProperties jwtProperties;
    private final SecurityProperties securityProperties;

    // 注入 JwtProperties SecurityProperties，不注入拦截器
    public WebMvcConfig(JwtProperties jwtProperties,SecurityProperties securityProperties) {
        this.jwtProperties = jwtProperties;
        this.securityProperties = securityProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenParseInterceptor tokenParseInterceptor() {
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

        // 白名单路径（不需要拦截）
        List<String> whitelist = securityProperties.getMergedWhitelist();

        // 获取 Token 解析拦截器（可能为 null，取决于配置）
        TokenParseInterceptor tokenInterceptor = null;
        try {
            tokenInterceptor = tokenParseInterceptor();
        } catch (Exception e) {
            log.debug("Token 解析拦截器未启用");
        }

        // Token 解析拦截器（优先级高，先执行）- 仅在启用时生效
        if (tokenInterceptor != null){
            registry.addInterceptor(tokenInterceptor)
                    .addPathPatterns("/**")
                    // 排除白名单
                    .excludePathPatterns(whitelist)
                    .order(1);
            log.debug("Token 解析拦截器已注册，白名单: {}", whitelist);
        }

        // 用户上下文拦截器（优先级低，后执行）
        registry.addInterceptor(userContextInterceptor())
                .addPathPatterns("/**")
                // 排除白名单
                .excludePathPatterns(whitelist)
                .order(2);
        log.debug("用户上下文拦截器已注册，白名单: {}", whitelist);
    }
}


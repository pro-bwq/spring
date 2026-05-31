package com.bwq.framework.core.config.knife4j;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author bwq
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ApiDocInfoProperties.class)
public class OpenApiConfig {

    /**
     * API 文档信息属性
     */
    private final ApiDocInfoProperties info;


    /**
     * OpenAPI 配置（元信息、安全协议）
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                            .title(info.getTitle())
                            .version(info.getVersion())
                            .description(info.getDescription())
                            .termsOfService(info.getTermsOfService())
                            .contact(new Contact()
                                .name(info.getContact().getName())
                                .url(info.getContact().getUrl())
                                .email(info.getContact().getEmail())
                            )
                            .license(new License().name(info.getLicense().getName())
                                .url(info.getLicense().getUrl())
                            ));
    }
}

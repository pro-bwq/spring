package com.bwq.framework.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author bwq
 * @date 2026-05-31 12:15:34
 * @description API 文档配置
 */

@Configuration
@ConditionalOnClass(OpenAPI.class)
public class Knife4jConfig {

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bwq Framework API")
                        .version("1.0.0")
                        .description("Bwq Spring Cloud 微服务架构 API 文档")
                        .contact(new Contact()
                                .name("bwq")
                                .url("https://github.com/bwq")
                                .email("2450790846@qq.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}

package com.bwq.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * @author bwq
 * @date 2026-06-03 11:11:48
 * @description JWT 配置属性
 */

@Data
@ConfigurationProperties(prefix = "bwq.jwt")
public class JwtProperties {

    /** JWT 签名密钥 */
    private String secret = "your-secret-key";

    /** Token 过期时间（毫秒） */
    private Long expiration = 3600000L;

    /** Token 解析拦截器是否启用（单体模式开启，微服务模式关闭） */
    private boolean tokenParseEnabled = false;
}

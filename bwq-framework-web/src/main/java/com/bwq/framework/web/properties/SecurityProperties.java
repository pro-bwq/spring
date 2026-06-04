package com.bwq.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bwq
 * @date 2026-06-04 12:14:22
 * @description 白名单配置
 */

@Data
@ConfigurationProperties(prefix = "bwq.security")
public class SecurityProperties {

    /**
     * 白名单路径（不需要 Token 认证）
     * 支持 Ant 风格通配符：
     * - ? 匹配单个字符
     * - * 匹配任意字符（不包含路径分隔符）
     * - ** 匹配任意路径
     */
    private List<String> whitelist = new ArrayList<>();

    /**
     * 是否启用白名单（默认 true）
     */
    private boolean whitelistEnabled = true;

    /**
     * 获取默认白名单（框架内置）
     */
    public List<String> getDefaultWhitelist() {
        List<String> defaults = new ArrayList<>();
        defaults.add("/swagger-resources/**");
        defaults.add("/webjars/**");
        defaults.add("/v3/api-docs/**");
        defaults.add("/v2/api-docs/**");
        defaults.add("/doc.html");
        defaults.add("/actuator/**");
        defaults.add("/error");
        return defaults;
    }

    /**
     * 获取合并后的白名单（默认 + 自定义）
     */
    public List<String> getMergedWhitelist() {
        List<String> merged = new ArrayList<>();
        merged.addAll(getDefaultWhitelist());
        if (whitelist != null) {
            merged.addAll(whitelist);
        }
        return merged;
    }
}

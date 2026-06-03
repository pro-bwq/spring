package com.bwq.framework.common.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bwq
 * @date 2026-06-03 12:17:55
 * @description 通用转换工具类
 */

public class CommonConvertUtil {

    private CommonConvertUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 将逗号分隔的字符串转换为 Set
     */
    public static Set<String> parseSet(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * 将 Set 转换为逗号分隔的字符串
     */
    public static String setToString(Set<String> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }
        return String.join(",", set);
    }

    /**
     * 安全转换为 Long
     */
    public static Long parseLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * null 转空字符串
     */
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * 从 Servlet 请求中提取 Bearer Token
     */
    public static String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 从 Reactive 请求中提取 Bearer Token
     */
    public static String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}

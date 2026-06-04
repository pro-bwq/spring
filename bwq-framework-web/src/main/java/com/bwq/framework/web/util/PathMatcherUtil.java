package com.bwq.framework.web.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;
/**
 * @author bwq
 * @date 2026-06-04 12:25:10
 * @description 路径匹配工具类（支持 Ant 风格通配符）
 */

public final class PathMatcherUtil {

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private PathMatcherUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 检查路径是否匹配白名单中的任何模式
     * @param path 请求路径
     * @param patterns 白名单模式列表（支持 Ant 风格）
     * @return 匹配返回 true
     */
    public static boolean matchesAny(String path, List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }

        for (String pattern : patterns) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查路径是否完全等于某个模式
     */
    public static boolean equalsAny(String path, List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        return patterns.contains(path);
    }
}

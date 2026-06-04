package com.bwq.framework.web.util;

import com.bwq.framework.web.annotation.Public;
import org.springframework.web.method.HandlerMethod;

/**
 * @author bwq
 * @date 2026-06-04 12:43:16
 * @description TODO
 */

public final class HandlerUtil {

    private HandlerUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 检查处理器是否被 @Public 标记
     */
    public static boolean isPublic(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 检查方法上是否有 @Public
            if (handlerMethod.getMethod().isAnnotationPresent(Public.class)) {
                return true;
            }

            // 检查类上是否有 @Public
            if (handlerMethod.getBeanType().isAnnotationPresent(Public.class)) {
                return true;
            }
        }
        return false;
    }
}

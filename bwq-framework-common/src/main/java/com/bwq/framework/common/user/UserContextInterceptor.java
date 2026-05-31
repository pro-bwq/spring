package com.bwq.framework.common.user;

import com.bwq.framework.common.constant.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bwq
 * @date 2026-05-30 01:12:32
 * @description TWeb 拦截器
 */

@Slf4j
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头解析用户信息
        String userId = request.getHeader(HeaderConstants.X_USER_ID);
        if (userId == null) {
            log.debug("请求头中无用户信息，可能为匿名请求");
            return true;
        }

        UserInfo user = UserInfo.builder()
                .userId(Long.parseLong(userId))
                .userName(request.getHeader(HeaderConstants.X_USER_NAME))
                .tenantId(parseLong(request.getHeader(HeaderConstants.X_TENANT_ID)))
                .avatarUrl(request.getHeader(HeaderConstants.X_AVATAR_URL))
                .roles(parseSet(request.getHeader(HeaderConstants.X_ROLES)))
                .permissions(parseSet(request.getHeader(HeaderConstants.X_PERMISSIONS)))
                .build();

        UserContext.setUser(user);
        log.debug("设置用户上下文: userId={}, tenantId={}", userId, user.getTenantId());
        return true;
    }

    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Set<String> parseSet(String header) {
        if (header == null || header.isEmpty()) {
            return null;
        }
        return Arrays.stream(header.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}

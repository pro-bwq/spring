package com.bwq.framework.web.interceptor;

import com.bwq.framework.common.constant.HeaderConstants;
import com.bwq.framework.common.user.UserContext;
import com.bwq.framework.common.user.UserInfo;
import com.bwq.framework.common.util.CommonConvertUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bwq
 * @date 2026-05-30 01:12:32
 * @description Web 拦截器
 */

@Slf4j
@Order(2)  // 优先级低于 TokenParseInterceptor
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头解析用户信息
        String userId = request.getHeader(HeaderConstants.X_USER_ID);

        // 无用户信息（匿名请求）
        if (userId == null || userId.isEmpty()) {
            log.debug("请求头中无用户信息，可能为匿名请求");
            return true;
        }

        // 如果 UserContext 已有用户信息（由 TokenParseInterceptor 设置），跳过重复设置
        if (UserContext.getUserId() != null) {
            log.debug("UserContext 已有用户信息，跳过请求头读取");
            return true;
        }

        // 构建用户信息
        UserInfo user = UserInfo.builder()
                .userId(CommonConvertUtil.parseLong(userId))
                .tenantId(CommonConvertUtil.parseLong(request.getHeader(HeaderConstants.X_TENANT_ID)))

                .userName(request.getHeader(HeaderConstants.X_USER_NAME))
                .roles(CommonConvertUtil.parseSet(request.getHeader(HeaderConstants.X_ROLES)))
                .permissions(CommonConvertUtil.parseSet(request.getHeader(HeaderConstants.X_PERMISSIONS)))
                .avatarUrl(request.getHeader(HeaderConstants.X_AVATAR_URL))
                .email(request.getHeader(HeaderConstants.X_EMAIL))
                .mobile(request.getHeader(HeaderConstants.X_MOBILE))
                .build();
        UserContext.setUser(user);
        log.debug("设置用户上下文: userId={}, tenantId={}", userId, user.getTenantId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}

package com.bwq.framework.web.interceptor;

import com.bwq.framework.common.constant.HeaderConstants;
import com.bwq.framework.common.user.UserContext;
import com.bwq.framework.common.user.UserInfo;
import com.bwq.framework.common.util.CommonConvertUtil;
import com.bwq.framework.web.util.HandlerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author bwq
 * @date 2026-05-30 01:12:32
 * @description Web 拦截器
 */

@Slf4j
@Order(2)  // 优先级低于 TokenParseInterceptor
@RequiredArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String path = request.getRequestURI();
        log.debug("==UserContextInterceptor，拦截器拦截====path={}",path);

        // 检查 @Public 注解
        if (HandlerUtil.isPublic(handler)) {
            log.debug("@Public 接口，跳过用户上下文设置: {}", path);
            return true;
        }

        // 从请求头解析用户信息
        String userId = request.getHeader(HeaderConstants.X_USER_ID);

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

package com.bwq.framework.web.interceptor;

import com.bwq.framework.common.user.JwtUtils;
import com.bwq.framework.common.user.UserInfo;
import com.bwq.framework.common.user.UserContext;
import com.bwq.framework.common.util.CommonConvertUtil;
import com.bwq.framework.web.properties.JwtProperties;
import com.bwq.framework.web.util.HandlerUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

/**
 * @author bwq
 * @date 2026-06-03 11:13:54
 * @description Token 解析拦截器,适用于单体应用，从 Authorization 头解析 JWT Token;如果检测到已有请求头用户信息（网关已处理），则跳过解析
 */

@Slf4j
@Component
@Order(1)  // 优先级高于 UserContextInterceptor
@ConditionalOnProperty(prefix = "bwq.jwt", name = "token-parse-enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class TokenParseInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String path = request.getRequestURI();
        log.debug("==TokenParseInterceptor，拦截器拦截====path={}",path);

        //  检查 @Public 注解
        if (HandlerUtil.isPublic(handler)) {
            log.debug("@Public 接口，跳过 Token 解析: {}", path);
            return true;
        }

        String token = CommonConvertUtil.extractToken(request);
        try {
            UserInfo user = JwtUtils.parseToken(token, jwtProperties.getSecret());
            UserContext.setUser(user);
            log.debug("Token 解析成功，用户ID: {}", user.getUserId());
            return true;

        } catch (ExpiredJwtException |SignatureException | MalformedJwtException e) {
            log.error("=====token异常=====");
            throw e;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 注意：不清空 UserContext，因为可能被后续拦截器继续使用
        // 最终由 UserContextInterceptor 的 afterCompletion 或 Filter 统一清理
    }
}

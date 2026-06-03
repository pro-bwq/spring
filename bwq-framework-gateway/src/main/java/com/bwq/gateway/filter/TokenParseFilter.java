package com.bwq.gateway.filter;

import com.bwq.framework.common.constant.HeaderConstants;
import com.bwq.framework.common.user.JwtUtils;
import com.bwq.framework.common.user.UserInfo;
import com.bwq.framework.common.util.CommonConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import java.nio.charset.StandardCharsets;

/**
 * @author bwq
 * @date 2026-05-30 01:20:33
 * @description 解析 token，放入请求头
 */

@Slf4j
@Component
@ConditionalOnProperty(prefix = "bwq.gateway", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TokenParseFilter implements GlobalFilter, Ordered{

    @Value("${bwq.jwt.secret:your-secret-key}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = CommonConvertUtil.extractToken(request);

        if (token == null) {
            log.debug("请求头中无 Token");
            return chain.filter(exchange);
        }

        try {
            UserInfo user = JwtUtils.parseToken(token, jwtSecret);
            if (user == null || user.getUserId() == null) {
                return unauthorizedResponse(exchange, "Token无效");
            }

            ServerHttpRequest newRequest = request.mutate()
                    .header(HeaderConstants.X_USER_ID, String.valueOf(user.getUserId()))
                    .header(HeaderConstants.X_TENANT_ID, String.valueOf(user.getTenantId()))
                    .header(HeaderConstants.X_USER_NAME, CommonConvertUtil.nullToEmpty(user.getUserName()))
                    .header(HeaderConstants.X_ROLES, CommonConvertUtil.setToString(user.getRoles()))
                    .header(HeaderConstants.X_PERMISSIONS, CommonConvertUtil.setToString(user.getPermissions()))
                    .header(HeaderConstants.X_AVATAR_URL, CommonConvertUtil.nullToEmpty(user.getAvatarUrl()))
                    .header(HeaderConstants.X_EMAIL, CommonConvertUtil.nullToEmpty(user.getEmail()))
                    .header(HeaderConstants.X_MOBILE, CommonConvertUtil.nullToEmpty(user.getMobile()))
                    .build();

            log.debug("Token 解析成功，用户ID: {}", user.getUserId());
            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (ExpiredJwtException e) {
            return unauthorizedResponse(exchange, "Token已过期，请重新登录");
        } catch (SignatureException e) {
            return unauthorizedResponse(exchange, "Token签名无效");
        } catch (MalformedJwtException e) {
            return unauthorizedResponse(exchange, "Token格式错误");
        } catch (Exception e) {
            log.error("Token 解析异常", e);
            return internalErrorResponse(exchange);
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> internalErrorResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        String body = "{\"code\":500,\"message\":\"系统内部错误\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public int getOrder() {
        // 优先级最高
        return -100;
    }

}

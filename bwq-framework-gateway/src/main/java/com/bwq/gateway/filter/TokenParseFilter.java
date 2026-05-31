package com.bwq.gateway.filter;

import com.bwq.framework.common.constant.HeaderConstants;
import com.bwq.framework.common.user.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * @author bwq
 * @date 2026-05-30 01:20:33
 * @description 解析 token，放入请求头
 */

@Slf4j
@Component
public class TokenParseFilter implements GlobalFilter, Ordered{

    @Value("${bwq.jwt.secret:your-secret-key}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = extractToken(request);

        if (token == null) {
            log.debug("请求头中无 Token");
            return chain.filter(exchange);
        }

        try {
            UserInfo user = parseToken(token);
            if (user == null) {
                return chain.filter(exchange);
            }

            ServerHttpRequest newRequest = request.mutate()
                    .header(HeaderConstants.X_USER_ID, String.valueOf(user.getUserId()))
                    .header(HeaderConstants.X_USER_NAME, user.getUserName() != null ? user.getUserName() : "")
                    .header(HeaderConstants.X_TENANT_ID, String.valueOf(user.getTenantId()))
                    .header(HeaderConstants.X_ROLES, user.getRoles() != null ? String.join(",", user.getRoles()) : "")
                    .header(HeaderConstants.X_PERMISSIONS, user.getPermissions() != null ? String.join(",", user.getPermissions()) : "")
                    .header(HeaderConstants.X_AVATAR_URL, user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                    .build();

            log.debug("Token 解析成功，用户ID: {}, 租户ID: {}", user.getUserId(), user.getTenantId());
            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (Exception e) {
            log.error("Token 解析失败: {}", e.getMessage());
            return chain.filter(exchange);
        }
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private UserInfo parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return UserInfo.builder()
                    .userId(Long.parseLong(claims.getSubject()))
                    .userName(claims.get("userName", String.class))
                    .tenantId(claims.get("tenantId", Long.class))
                    .roles(parseSet(claims.get("roles", String.class)))
                    .permissions(parseSet(claims.get("permissions", String.class)))
                    .avatarUrl(claims.get("avatarUrl", String.class))
                    .build();
        } catch (Exception e) {
            log.error("JWT 解析失败", e);
            return null;
        }

    }

    private Set<String> parseSet(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public int getOrder() {
        // 优先级最高
        return -100;
    }

}

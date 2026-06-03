package com.bwq.framework.common.user;

import com.bwq.framework.common.constant.HeaderConstants;
import com.bwq.framework.common.util.CommonConvertUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import java.nio.charset.StandardCharsets;

/**
 * @author bwq
 * @date 2026-06-03 11:47:15
 * @description JWT 工具类。提供 Token 解析和用户信息提取功能
 */

@Slf4j
public final class JwtUtils {

    private JwtUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 解析 Token 并提取用户信息
     *
     * @param token  JWT Token
     * @param secret 签名密钥
     * @return 用户信息，解析失败返回 null
     */
    public static UserInfo parseToken(String token, String secret) throws ExpiredJwtException, SignatureException, MalformedJwtException{
        if (!StringUtils.hasText(token)) {
            throw new MalformedJwtException("Token is empty");
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return UserInfo.builder()
                .userId(getLong(claims, "userId"))
                .tenantId(getLong(claims, "tenantId"))
                .userName(getString(claims, HeaderConstants.X_USER_NAME))
                .roles(CommonConvertUtil.parseSet(getString(claims, HeaderConstants.X_ROLES)))

                .permissions(CommonConvertUtil.parseSet(getString(claims, HeaderConstants.X_PERMISSIONS)))

                .avatarUrl(getString(claims, HeaderConstants.X_AVATAR_URL))
                .email(getString(claims, HeaderConstants.X_EMAIL))
                .mobile(getString(claims, HeaderConstants.X_MOBILE))
                .build();
    }

    /**
     * 验证 Token 是否有效
     */
    public static boolean validateToken(String token, String secret) {
        try {
            parseToken(token, secret);
            return true;
        } catch (Exception e) {
            log.debug("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }



    private static Long getLong(Claims claims, String key) {
        Object value = claims.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String getString(Claims claims, String key) {
        Object value = claims.get(key);
        return value != null ? value.toString() : null;
    }

}
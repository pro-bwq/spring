package com.bwq.framework.web.aspect;

import com.bwq.framework.core.exception.BusinessException;
import com.bwq.framework.common.user.UserContext;
import com.bwq.framework.redis.core.RedisHelper;
import com.bwq.framework.web.annotation.PreventDuplicate;
import com.bwq.framework.web.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
/**
 * @author bwq
 * @date 2026-06-04 17:10:12
 * @description AOP 切面实现
 */

@Slf4j
@Aspect
@RequiredArgsConstructor
@Order(1)
public class PreventDuplicateAspect {

    private final RedisHelper redisHelper;

    @Around("@within(com.bwq.framework.web.annotation.PreventDuplicate) || " +
            "@annotation(com.bwq.framework.web.annotation.PreventDuplicate)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // RedisHelper 未配置时降级（不启用防重）
        if (redisHelper == null) {
            log.warn("Redis 未配置，防重提交功能已降级");
            return joinPoint.proceed();
        }

        // 获取注解配置
        PreventDuplicate annotation = getAnnotation(joinPoint);
        if (annotation == null) {
            return joinPoint.proceed();
        }

        // 生成防重 Key
        String key = buildKey(joinPoint, annotation);

        // 尝试设置锁（使用 setIfAbsent 语义）
        // RedisHelper 需要提供 setIfAbsent 方法，如果不存在则返回 true
        boolean acquired = tryAcquireLock(key, annotation.expire(), annotation.timeUnit());

        if (!acquired) {
            log.warn("检测到重复提交，Key: {}", key);
            throw new BusinessException(409, annotation.message());
        }

        log.debug("防重锁获取成功，Key: {}, 过期时间: {} {}",
                key, annotation.expire(), annotation.timeUnit());

        return joinPoint.proceed();
    }

    /**
     * 尝试获取锁
     *
     * 注意：RedisHelper 当前没有 setIfAbsent 方法，需要补充
     * 或者使用 RedisHelper.setNx() 方法
     */
    private boolean tryAcquireLock(String key, long expire, TimeUnit timeUnit) {
        return redisHelper.setIfAbsent(key, "1", expire, timeUnit);
    }

    /**
     * 获取注解（支持类级别和方法级别）
     */
    private PreventDuplicate getAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 优先获取方法上的注解
        PreventDuplicate annotation = AnnotationUtils.findAnnotation(method, PreventDuplicate.class);
        if (annotation != null) {
            return annotation;
        }

        // 获取类上的注解
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return AnnotationUtils.findAnnotation(targetClass, PreventDuplicate.class);
    }

    /**
     * 构建防重 Key
     * 格式：{prefix}:{userIdOrIp}:{apiPath}:{paramHash}
     */
    private String buildKey(ProceedingJoinPoint joinPoint, PreventDuplicate annotation) {
        String prefix = annotation.prefix();
        String apiPath = RequestUtils.getRequestUri();

        // 用户标识：优先使用 userId，否则使用 IP + UserAgent
        String userIdentifier = getUserIdentifier();

        // 参数 Hash
        String paramHash = buildParamHash(joinPoint);

        String key = String.format("%s:%s:%s:%s",
                prefix, userIdentifier, apiPath, paramHash);

        // 控制 Key 长度，避免过长
        if (key.length() > 200) {
            key = DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
        }

        return key;
    }

    /**
     * 获取用户标识
     */
    private String getUserIdentifier() {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            return "user:" + userId;
        }

        // 未登录用户使用 IP + UserAgent
        String ip = RequestUtils.getClientIp();
        String ua = RequestUtils.getUserAgent();
        String identifier = ip + ":" + (ua != null ? ua : "unknown");
        return "anon:" + DigestUtils.md5DigestAsHex(identifier.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 构建参数 Hash
     */
    private String buildParamHash(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "no_params";
        }

        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                sb.append(arg.hashCode());
            }
        }

        String rawParams = sb.toString();
        return DigestUtils.md5DigestAsHex(rawParams.getBytes(StandardCharsets.UTF_8));
    }
}

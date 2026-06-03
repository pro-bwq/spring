package com.bwq.framework.db.mybatis.aspect;

import com.bwq.framework.common.user.UserContext;
import com.bwq.framework.db.mybatis.annotation.IgnoreTenant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author bwq
 * @date 2026-06-03 22:43:29
 * @description 忽略租户注解的 AOP 切面，检测到 @IgnoreTenant 注解时，设置忽略租户标记
 */

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)  // 最高优先级，确保最先执行
public class IgnoreTenantAspect {

    /**
     * 切点：类上标注了 @IgnoreTenant 或方法上标注了 @IgnoreTenant
     */
    @Pointcut("@within(com.bwq.framework.db.mybatis.annotation.IgnoreTenant) || " +
            "@annotation(com.bwq.framework.db.mybatis.annotation.IgnoreTenant)")
    public void ignoreTenantPointcut() {
    }

    /**
     * 环绕通知：在方法执行前后设置/清除忽略租户标记
     */
    @Around("ignoreTenantPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 设置忽略租户标记
            UserContext.setIgnoreTenant(true);
            log.debug("检测到 @IgnoreTenant 注解，当前线程忽略租户过滤 - 目标: {}.{}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName());

            // 执行原方法
            return joinPoint.proceed();

        } finally {
            // 清除忽略租户标记
            UserContext.clearIgnoreTenant();
            log.debug("清除租户忽略标记 - 目标: {}.{}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName());
        }
    }
}

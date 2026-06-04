package com.bwq.framework.web.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author bwq
 * @date 2026-06-04 17:09:24
 * @description 防重提交注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventDuplicate {

    /**
     * 防重锁过期时间（秒），默认 3 秒
     */
    long expire() default 3;

    /**
     * 时间单位（默认秒）
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 自定义 Key 前缀
     */
    String prefix() default "prevent:duplicate";

    /**
     * 错误提示信息
     */
    String message() default "操作过于频繁，请稍后再试";
}
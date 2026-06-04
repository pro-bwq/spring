package com.bwq.framework.web.annotation;

import java.lang.annotation.*;
/**
 * @author bwq
 * @date 2026-06-04 12:23:58
 * @description 标记为公开接口，不需要 Token 认证
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Public {

}
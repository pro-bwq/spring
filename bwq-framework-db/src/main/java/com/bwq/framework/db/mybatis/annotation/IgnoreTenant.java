package com.bwq.framework.db.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author bwq
 * @date 2026-05-30 01:19:34
 * @description 忽略多租户隔离，标注此注解的方法或类，SQL 执行时不会添加租户过滤条件
 *
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreTenant {

}

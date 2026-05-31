package com.bwq.framework.web.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.lang.annotation.*;

/**
 * @author bwq
 * @date 2026-05-31 12:17:03
 * @description API 版本控制
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
@RequestMapping
public @interface ApiVersion {

    @AliasFor(annotation = RequestMapping.class, attribute = "path")
    String[] value() default {};

    String version() default "v1";
}

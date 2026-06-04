package com.bwq.ecommerce.validation.annotation;

import com.bwq.ecommerce.validation.validator.IdCardValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author bwq
 * @date 2026-06-04 09:32:57
 * @description 校验身份证号码
 */

@Target({ElementType.METHOD, ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IdCardValidator.class)
public @interface IdCard {

    String message() default "身份证号码格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // 是否严格校验（包括校验位）
    boolean strict() default true;
}

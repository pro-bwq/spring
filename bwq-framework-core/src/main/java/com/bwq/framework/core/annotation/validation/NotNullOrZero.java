package com.bwq.framework.core.annotation.validation;

import com.bwq.framework.core.annotation.validator.NotNullOrZeroValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author bwq
 * @date 2026-06-04 09:32:57
 * @description 校验值不能为 null 或 0
 */

@Documented
@Constraint(validatedBy = NotNullOrZeroValidator.class)
@Target({ElementType.PARAMETER  // 方法参数上
        , ElementType.FIELD      // 类属性上
})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullOrZero {
    String message() default "值不能为 null 或 0";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package com.bwq.framework.core.annotation.validation;

import com.bwq.framework.core.annotation.validator.LongitudeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author bwq
 * @date 2026-06-04 09:32:57
 * @description 校验精度（坐标）
 */
@Documented
@Constraint(validatedBy = LongitudeValidator.class)
@Target({ ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Longitude {
    String message() default "经度值必须在 -180 到 180 之间";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // 最小经度
    double min() default -180.0;

    // 最大经度
    double max() default 180.0;
}

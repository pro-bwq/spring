package com.bwq.framework.core.annotation.validation;

import com.bwq.framework.core.annotation.validator.LatitudeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author bwq
 * @date 2026-06-04 09:32:57
 * @description 校验维度（坐标）
 */

@Target({ ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LatitudeValidator.class)
public @interface Latitude {
    String message() default "纬度值必须在 -90 到 90 之间";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // 最小纬度
    double min() default -90.0;

    // 最大纬度
    double max() default 90.0;
}

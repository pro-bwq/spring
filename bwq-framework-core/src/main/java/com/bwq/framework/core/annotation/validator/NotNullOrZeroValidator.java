package com.bwq.framework.core.annotation.validator;

import com.bwq.framework.core.annotation.validation.NotNullOrZero;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * @author BWQ
 * @description:
 * @date 2025/4/19 14:22
 */
public class NotNullOrZeroValidator implements ConstraintValidator<NotNullOrZero, Number> {
    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }
        // 处理不同类型的数值
        if (value instanceof Integer) {
            return value.intValue() != 0;
        } else if (value instanceof Long) {
            return value.longValue() != 0L;
        } else if (value instanceof Double) {
            return value.doubleValue() != 0.0;
        } else if (value instanceof Float) {
            return value.floatValue() != 0.0f;
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).compareTo(BigDecimal.ZERO) != 0;
        } else if (value instanceof Short) {
            return value.shortValue() != 0;
        } else if (value instanceof Byte) {
            return value.byteValue() != 0;
        }
        return true;
    }
}

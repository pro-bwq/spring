package com.bwq.framework.core.annotation.validator;

import com.bwq.framework.core.annotation.validation.Longitude;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author BWQ
 * @description:
 * @date 2025/4/7 11:32
 */
public class LongitudeValidator implements ConstraintValidator<Longitude, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(Longitude constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Double longitude, ConstraintValidatorContext context) {
        if (longitude == null) {
            return false;
        }
        return longitude >= min && longitude <= max;
    }
}

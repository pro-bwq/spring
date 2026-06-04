package com.bwq.framework.core.annotation.validator;

import com.bwq.framework.core.annotation.validation.Latitude;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author BWQ
 * @description:
 * @date 2025/4/7 11:32
 */
public class LatitudeValidator implements ConstraintValidator<Latitude, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(Latitude constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Double latitude, ConstraintValidatorContext context) {
        if (latitude == null) {
            return false;
        }
        return latitude >= min && latitude <= max;
    }
}

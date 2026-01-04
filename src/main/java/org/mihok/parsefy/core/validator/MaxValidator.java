package org.mihok.parsefy.core.validator;

import org.mihok.parsefy.validation.ValidationResult;

public class MaxValidator implements FieldValidator<Number> {
    private final long maxValue;
    private final String message;

    public MaxValidator(long maxValue, String message) {
        this.maxValue = maxValue;
        this.message = message.replace("{value}", String.valueOf(maxValue));
    }

    @Override
    public ValidationResult validate(Number value) {
        if (value != null && value.longValue() > maxValue) {
            return ValidationResult.error(message);
        }
        return ValidationResult.success();
    }
}
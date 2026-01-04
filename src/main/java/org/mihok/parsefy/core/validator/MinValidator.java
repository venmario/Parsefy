package org.mihok.parsefy.core.validator;

import org.mihok.parsefy.validation.ValidationResult;

public class MinValidator implements FieldValidator<Number> {
    private final long minValue;
    private final String message;

    public MinValidator(long minValue, String message) {
        this.minValue = minValue;
        this.message = message.replace("{value}", String.valueOf(minValue));
    }

    @Override
    public ValidationResult validate(Number value) {
        if (value != null && value.longValue() < minValue) {
            return ValidationResult.error(message);
        }
        return ValidationResult.success();
    }
}

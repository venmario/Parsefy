package org.mihok.parsefy.core.validator;

import org.mihok.parsefy.validation.ValidationResult;

public class NotBlankValidator implements FieldValidator<String> {
    private final String message;

    public NotBlankValidator(String message) {
        this.message = message;
    }

    @Override
    public ValidationResult validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(message);
        }
        return ValidationResult.success();
    }
}

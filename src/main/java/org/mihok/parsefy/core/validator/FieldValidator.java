package org.mihok.parsefy.core.validator;

import org.mihok.parsefy.validation.ValidationResult;

public interface FieldValidator<T> {
    ValidationResult validate(T value);
}

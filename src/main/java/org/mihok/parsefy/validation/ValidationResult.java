package org.mihok.parsefy.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public static ValidationResult success() {
        return new ValidationResult();
    }

    public static ValidationResult error(String message) {
        ValidationResult result = new ValidationResult();
        result.addError(message);
        return result;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void merge(ValidationResult other) {
        this.errors.addAll(other.errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}

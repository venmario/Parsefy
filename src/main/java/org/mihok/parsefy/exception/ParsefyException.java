package org.mihok.parsefy.exception;

import org.mihok.parsefy.core.RowError;

import java.util.List;

public class ParsefyException extends Exception {
    private final List<RowError> errors;

    public ParsefyException(String message, List<RowError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<RowError> getErrors() {
        return errors;
    }
}
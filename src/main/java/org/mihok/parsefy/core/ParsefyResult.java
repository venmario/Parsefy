package org.mihok.parsefy.core;

import org.mihok.parsefy.exception.ParsefyException;

import java.util.List;

public class ParsefyResult<T> {

    private List<RowError> errors;
    private List<T> validRows;


    public List<T> getValidRows() {
        return this.validRows;
    }

    public void setValidRows(List<T> validRows) {
        this.validRows = validRows;
    }

    public List<RowError> getErrors() {
        return this.errors;
    }

    public void setErrors(List<RowError> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void throwIfErrors() throws ParsefyException {
        if (hasErrors()) {
            throw new ParsefyException("Parsing failed with errors", errors);
        }
    }
}

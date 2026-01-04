package org.mihok.parsefy.core;

import java.util.List;

public class RowError {
    private final long rowNumber;
    private final String fieldName;
    private final List<String> errors;

    public RowError(long rowNumber, String fieldName, List<String> errors) {
        this.rowNumber = rowNumber;
        this.fieldName = fieldName;
        this.errors = errors;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public String getFieldName() {
        return fieldName;
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return String.format("Row %d, Field '%s': %s",
                rowNumber, fieldName, String.join(", ", errors));
    }
}
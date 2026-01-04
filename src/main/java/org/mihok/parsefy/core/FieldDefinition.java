package org.mihok.parsefy.core;

import org.mihok.parsefy.core.validator.FieldValidator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldDefinition {
    private String name;
    private boolean required;
    private Field field;
    private final List<FieldValidator<?>> validators = new ArrayList<>();

    public String getColumnName() {
        return this.name;
    }

    public void setColumnName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public List<FieldValidator<?>> getValidators() {
        return this.validators;
    }

    public void addValidator(FieldValidator<?> validator) {
        this.validators.add(validator);
    }

    public void setValue(Object object, Object value) throws IllegalAccessException {
        this.field.set(object, value);
    }


    public <T> Object getValue(T instance) throws IllegalAccessException {
        return field.get(instance);
    }
}

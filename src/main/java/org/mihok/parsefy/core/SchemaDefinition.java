package org.mihok.parsefy.core;

import java.util.ArrayList;
import java.util.List;

public class SchemaDefinition<T> {
    private final Class<T> clazz;
    private final List<FieldDefinition> fieldDefinitions;

    public SchemaDefinition(Class<T> clazz) {
        this.clazz = clazz;
        this.fieldDefinitions = new ArrayList<>();
    }

    public Class<T> getClazz(){
        return this.clazz;
    }

    public List<FieldDefinition> getFields() {
        return this.fieldDefinitions;
    }

    public void addField(FieldDefinition fieldDef) {
        fieldDefinitions.add(fieldDef);
    }
}

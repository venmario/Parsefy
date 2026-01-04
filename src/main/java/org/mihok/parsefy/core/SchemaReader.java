package org.mihok.parsefy.core;

import org.mihok.parsefy.*;
import org.mihok.parsefy.core.validator.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class SchemaReader {

    public static <T> SchemaDefinition<T> read(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(CsvSchema.class)) {
            throw new IllegalArgumentException("Class should annotated with CsvSchema Annotation");
        }

        SchemaDefinition<T> schema = new SchemaDefinition<>(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(CsvColumn.class)) {
                FieldDefinition fieldDef = new FieldDefinition();

                CsvColumn csvColumn = field.getAnnotation(CsvColumn.class);
                fieldDef.setColumnName(csvColumn.name());
                fieldDef.setRequired(csvColumn.required());
                fieldDef.setField(field);

                if (field.isAnnotationPresent(NotBlank.class)){
                    NotBlank notBlank = field.getAnnotation(NotBlank.class);
                    fieldDef.addValidator(new NotBlankValidator(notBlank.message()));
                }
                if (field.isAnnotationPresent(Min.class)) {
                    Min annotation = field.getAnnotation(Min.class);
                    fieldDef.addValidator(new MinValidator(annotation.value(), annotation.message()));
                }

                if (field.isAnnotationPresent(Max.class)) {
                    Max annotation = field.getAnnotation(Max.class);
                    fieldDef.addValidator(new MaxValidator(annotation.value(), annotation.message()));
                }

                if (field.isAnnotationPresent(Email.class)) {
                    Email annotation = field.getAnnotation(Email.class);
                    fieldDef.addValidator(new EmailValidator(annotation.message()));
                }

                if (field.isAnnotationPresent(CustomValidator.class)) {
                    CustomValidator annotation = field.getAnnotation(CustomValidator.class);
                    try {
                        Constructor<?> constructor = annotation.value().getDeclaredConstructor();
                        FieldValidator<?> validator = (FieldValidator<?>) constructor.newInstance();
                        fieldDef.addValidator(validator);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate custom validator", e);
                    }
                }

                schema.addField(fieldDef);
            }
        }
        return schema;
    }
}

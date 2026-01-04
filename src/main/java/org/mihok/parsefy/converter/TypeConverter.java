package org.mihok.parsefy.converter;

import org.mihok.parsefy.DateFormat;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TypeConverter {
    public static Object convert(String value, Class<?> targetType, Field field) throws RuntimeException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        value = value.trim();
        try {
            if (targetType.equals(String.class)) {
                return value;
            }
            if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
                return Integer.parseInt(value);
            }
            if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(value);
            }
            if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(value);
            }
            if (targetType == BigDecimal.class) {
                return new BigDecimal(value);
            }
            if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.parseBoolean(value);
            }
            if (targetType == LocalDate.class) {
                DateFormat dateFormat = field.getAnnotation(DateFormat.class);
                String pattern = dateFormat != null ? dateFormat.value() : "yyyy-MM-dd";
                return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert value '" + value + "' to type " + targetType.getSimpleName(), e);
        }
        throw new RuntimeException("Unsupported type: " + targetType);
    }
}

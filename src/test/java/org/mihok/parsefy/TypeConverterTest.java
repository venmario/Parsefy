package org.mihok.parsefy;

import org.junit.jupiter.api.Test;
import org.mihok.parsefy.converter.TypeConverter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TypeConverterTest {
    @CsvSchema
    static class Employee {
        @CsvColumn(name = "name")
        private String name;

        @CsvColumn(name = "age")
        private Integer age;

        public Integer getAge() {
            return this.age;
        }
    }

    @Test
    public void shouldConvertStringToInteger() throws NoSuchFieldException {
        String age = "25";

        Field field = Employee.class.getDeclaredField("age");
        Object value = TypeConverter.convert(age, Integer.class, field);
        assertEquals(25, value);
    }

    @Test
    void shouldConvertStringToBigDecimal() {
        String value = "123.45";
        Object result = TypeConverter.convert(value, BigDecimal.class, null);

        assertEquals(new BigDecimal("123.45"), result);
    }

    @Test
    void shouldConvertStringToLocalDate() throws NoSuchFieldException {
        String value = "20240115";
        String value2 = "20240115";
        @CsvSchema
        class DateDto {
            @DateFormat("yyyyMMdd")
            private LocalDate date;

            @DateFormat
            private LocalDate date2;
        }

        Field field = DateDto.class.getDeclaredField("date");
        Object result = TypeConverter.convert(value, LocalDate.class, field);
        Object result2 = TypeConverter.convert(value, LocalDate.class, field);

        assertEquals(LocalDate.of(2024, 1, 15), result);
        assertEquals(LocalDate.of(2024, 1, 15), result2);
    }

    @Test
    void shouldThrowExceptionForInvalidInteger() {
        assertThrows(RuntimeException.class, () -> {
            TypeConverter.convert("not-a-number", Integer.class, null);
        });
    }
}

package org.mihok.parsefy;

import org.junit.jupiter.api.Test;
import org.mihok.parsefy.core.Parsefy;
import org.mihok.parsefy.core.validator.*;
import org.mihok.parsefy.validation.ValidationResult;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {

    @CsvSchema
    public static class Person {
        @CsvColumn(name = "name", required = true)
        private String name;

        @CsvColumn(name = "age")
        private Integer age;

        @CsvColumn(name = "job", required = true)
        private String job;

        public String getName() {
            return this.name;
        }
    }

    @Test
    public void shouldPassForNonRequiredFields() throws Exception {
        String csv = "name,job\nmario,software engineer";

        List<Person> result = Parsefy.builder(Person.class).parse(new ByteArrayInputStream(csv.getBytes())).getResult().getValidRows();

        assertEquals(1, result.size());
    }

    @Test
    public void shouldFailedForNonRequiredFields() {
        String csv = "name,age\nmario,25";

        Exception exception = assertThrows(Exception.class, () -> {
            Parsefy.builder(Person.class).parse(new ByteArrayInputStream(csv.getBytes()));
        });

        assertEquals("Required field 'job' is missing", exception.getMessage());

        String csv2 = "age\n25";

        exception = assertThrows(Exception.class, () -> {
            Parsefy.builder(Person.class).parse(new ByteArrayInputStream(csv2.getBytes()));
        });

        assertEquals("Required field 'name' is missing", exception.getMessage());
    }

    @Test
    public void shouldPassForNonBlankString() {
        NotBlankValidator validator = new NotBlankValidator("Cannot be blank");

        ValidationResult result = validator.validate("John");

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void shouldFailForNullString() {
        NotBlankValidator validator = new NotBlankValidator("Cannot be blank");
        ValidationResult result = validator.validate(null);

        assertFalse(result.isValid());
        assertEquals("Cannot be blank", result.getErrors().get(0));
    }

    @Test
    public void shouldFailForEmptyString() {
        NotBlankValidator validator = new NotBlankValidator("Cannot be blank");

        ValidationResult result = validator.validate("");

        assertFalse(result.isValid());
    }

    @Test
    public void shouldFailForWhitespaceOnlyString() {
        NotBlankValidator validator = new NotBlankValidator("Cannot be blank");

        ValidationResult result = validator.validate("   ");

        assertFalse(result.isValid());
    }

    @Test
    public void shouldFailLessThanMinimalValue() {
        MinValidator validator = new MinValidator(3, "Value cannot be less than {value}");

        ValidationResult result = validator.validate(2);
        assertFalse(result.isValid());
        assertEquals("Value cannot be less than 3", result.getErrors().get(0));
    }

    @Test
    public void shouldPassGreaterThanMinimalValue() {
        MinValidator validator = new MinValidator(3, "Value cannot be less than {value}");

        ValidationResult result = validator.validate(5);
        assertTrue(result.isValid());
    }

    @Test
    public void shouldFailGreaterThanMaximumValue() {
        MaxValidator validator = new MaxValidator(3, "Value cannot be greater than {value}");

        ValidationResult result = validator.validate(5);
        assertFalse(result.isValid());
        assertEquals("Value cannot be greater than 3", result.getErrors().get(0));
    }

    @Test
    public void shouldPassLessThanMaximumValue() {
        MaxValidator validator = new MaxValidator(3, "Value cannot be less than {value}");

        ValidationResult result = validator.validate(2);
        assertTrue(result.isValid());
    }

    @Test
    public void shouldPassForCorrectEmailFormat() {
        EmailValidator validator = new EmailValidator("Email format is not correct");
        ValidationResult result = validator.validate("bjorka@gmail.com");
        assertTrue(result.isValid());

        result = validator.validate("bjorka@gmail.co.id");
        assertTrue(result.isValid());

        result = validator.validate("bjor.ka@gmail.co.id");
        assertTrue(result.isValid());
    }

    @Test
    public void shouldFailedForIncorrectEmailFormat() {
        EmailValidator validator = new EmailValidator("Email format is not correct");
        ValidationResult result = validator.validate("bjorka@gmail");
        assertFalse(result.isValid());

        result = validator.validate("@gmail.com");
        assertFalse(result.isValid());

        result = validator.validate("bjorka.gmail.com");
        assertFalse(result.isValid());

        result = validator.validate("bj.0r.k4@gmailcom");
        assertFalse(result.isValid());
    }

    public static class VendorCodeValidator implements FieldValidator<String> {

        private final String vcPrefix;
        private final String message;

        public VendorCodeValidator(String vcPrefix, String message) {
            this.vcPrefix = vcPrefix;
            this.message = message.replace("{value}", vcPrefix);
        }

        @Override
        public ValidationResult validate(String value) {
            if (!value.contains(vcPrefix)) {
                return ValidationResult.error(message);
            }
            return ValidationResult.success();
        }
    }

    @Test
    public void shouldPassWhenVendorCodeContainsVC() {
        VendorCodeValidator validator = new VendorCodeValidator("VC", "Vendor Code should contains '{value}'");

        ValidationResult result = validator.validate("VC1234");

        assertTrue(result.isValid());
    }

    @Test
    public void shouldFailWhenVendorCodeContainsVC() {
        VendorCodeValidator validator = new VendorCodeValidator("VC", "Vendor Code should contains '{value}'");

        ValidationResult result = validator.validate("VS1234");

        assertFalse(result.isValid());
        assertEquals("Vendor Code should contains 'VC'", result.getErrors().get(0));
    }
}

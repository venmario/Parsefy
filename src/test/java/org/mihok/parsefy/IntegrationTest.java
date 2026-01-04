package org.mihok.parsefy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mihok.parsefy.core.Parsefy;
import org.mihok.parsefy.core.ParsefyResult;

import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;

public class IntegrationTest {
    @CsvSchema
    public static class Person {
        @NotBlank
        @CsvColumn(name = "name", required = true)
        private String name;
        @NotBlank
        @CsvColumn(name = "job")
        private String job;

        @Min(value = 18)
        @Max(value = 30)
        @CsvColumn(name = "age")
        private Integer age;

        @DateFormat
        @CsvColumn(name = "birth_date")
        private LocalDate birthDate;
    }

    @Test
    void shouldPass() throws Exception {
        String csv = "name,job,age,birth_date\nmario,software engineer,25,2000-10-19\nbudi,teacher,30,1990-12-20";
        Reader reader = new StringReader(csv);
        ParsefyResult<Person> result = Parsefy.builder(Person.class).strictMode(true).parse(reader).getResult();
        List<Person> validrows = result.getValidRows();
        Assertions.assertEquals(2, validrows.size());
        Assertions.assertEquals(0, result.getErrors().size());
    }

    @Test
    void shouldThrowException() throws Exception {
        String csv = "name,job,age,birth_date\nmario,software engineer,25,2000-10-19\nbudi,teacher,35,1990-12-20";
        Reader reader = new StringReader(csv);
        Exception exception = Assertions.assertThrows(Exception.class, () -> Parsefy.builder(Person.class).strictMode(true).parse(reader));
    }

    @Test
    void shouldHave1ErrorRow() throws Exception {
        String csv = "name,job,age,birth_date\nmario,software engineer,25,2000-10-19\nbudi,teacher,35,1990-12-20";
        Reader reader = new StringReader(csv);
        ParsefyResult<Person> result = Parsefy.builder(Person.class).strictMode(false).parse(reader).getResult();
        Assertions.assertEquals(1, result.getValidRows().size());
        Assertions.assertEquals(1, result.getErrors().size());
    }
}

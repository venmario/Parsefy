package org.mihok.parsefy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mihok.parsefy.core.Parsefy;
import org.mihok.parsefy.dto.Wafer;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A simple unit test
 */
public class AnnotationTest {

    static Class<Wafer> clazz;

    @BeforeAll
    public static void schemaShouldAnnotatedWithCsvSchemaAnnotation() {
        clazz = Wafer.class;
        CsvSchema csvSchema = clazz.getAnnotation(CsvSchema.class);
        assertNotNull(csvSchema);
    }

    @Test
    public void csvColumnAnnotationShouldExist() throws NoSuchFieldException {
        Field fab = clazz.getDeclaredField("fab");
        CsvColumn csvColumn = fab.getAnnotation(CsvColumn.class);

        assertNotNull(csvColumn);

        assertNotNull(csvColumn.name());
        assertEquals("fab", csvColumn.name());

        Field id = clazz.getDeclaredField("id");
        csvColumn = id.getAnnotation(CsvColumn.class);

        assertNull(csvColumn);
    }

    @Test
    void shouldMapColumnsByAnnotation() throws Exception {
        String csv = "fab,vendor_code\nSG08,123456";

        List<Wafer> result = Parsefy.builder(Wafer.class)
                .parse(new ByteArrayInputStream(csv.getBytes())).getResult().getValidRows();

        assertEquals("SG08", result.get(0).getFab());
        assertEquals("123456", result.get(0).getVendorCode());
    }

    @Test
    void shouldFailWhenFieldIsBlankThatAnnotatedByNotBlank() throws Exception {
        String csv = "fab,vendor_code\nSG08,123456\nSG18,";

        assertThrows(Exception.class, () -> Parsefy.builder(Wafer.class)
                .parse(new ByteArrayInputStream(csv.getBytes())));
    }

}

package org.mihok.parsefy;

import org.junit.jupiter.api.Test;
import org.mihok.parsefy.core.Parsefy;
import org.mihok.parsefy.dto.Wafer;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParsefyTest {

    @Test
    void shouldParseEmptyCsv() throws Exception {
        String csv = "";

        List<Wafer> result = Parsefy.builder(Wafer.class)
                .parse(new ByteArrayInputStream(csv.getBytes()))
                .getResult()
                .getValidRows();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldParseSingleRow() throws Exception {
        String csv = "fab\nSG18";

        List<Wafer> result = Parsefy.builder(Wafer.class)
                .parse(new ByteArrayInputStream(csv.getBytes()))
                .getResult()
                .getValidRows();

        assertEquals(1, result.size());
        assertEquals("SG18", result.get(0).getFab());
    }

    @Test
    void shouldParseMultiRow() throws Exception {
        String csv = "fab\nSG18\nSG08\nSG12";

        List<Wafer> result = Parsefy.builder(Wafer.class)
                .parse(new ByteArrayInputStream(csv.getBytes()))
                .getResult()
                .getValidRows();

        assertEquals(3, result.size());
        assertEquals("SG18", result.get(0).getFab());
        assertEquals("SG08", result.get(1).getFab());
        assertEquals("SG12", result.get(2).getFab());
    }
}

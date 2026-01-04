package org.mihok.parsefy.dto;

import org.mihok.parsefy.CsvColumn;
import org.mihok.parsefy.CsvSchema;
import org.mihok.parsefy.NotBlank;

@CsvSchema
public class Wafer {
    private Long id;

    @CsvColumn(name = "fab")
    private String fab;

    @NotBlank
    @CsvColumn(name = "vendor_code")
    private String vendorCode;

    public String getFab() {
        return this.fab;
    }

    public String getVendorCode() {
        return this.vendorCode;
    }

    @Override
    public String toString() {
        return "Wafer[fab=" + this.fab + "]";
    }
}

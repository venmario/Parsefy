package org.mihok.parsefy.core;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mihok.parsefy.converter.TypeConverter;
import org.mihok.parsefy.core.validator.FieldValidator;
import org.mihok.parsefy.validation.ValidationResult;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parsefy<T> {
    private Charset defaultCharset = StandardCharsets.UTF_8;
    private String delimiter = ",";
    private boolean skipHeader = true;
    private boolean strictMode = true;
    private boolean trim = true;
    private final Map<Class<?>, Object> dependencies = new HashMap<>();
    private final Class<T> schemaClass;
    private final ParsefyResult<T> result;

    private Parsefy(Class<T> schemaClass) {
        this.schemaClass = schemaClass;
        this.result = new ParsefyResult<>();
    }

    public static <T> Parsefy<T> builder(Class<T> clazz) {
        return new Parsefy<>(clazz);
    }

    public Parsefy<T> delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public Parsefy<T> skipHeader(boolean skipHeader) {
        this.skipHeader = skipHeader;
        return this;
    }

    public Parsefy<T> strictMode(boolean strictMode) {
        this.strictMode = strictMode;
        return this;
    }

    public Parsefy<T> trim(boolean trim) {
        this.trim = trim;
        return this;
    }

    public <D> Parsefy<T> addDependency(Class<D> type, D instance) {
        this.dependencies.put(type, instance);
        return this;
    }

    public Parsefy<T> parse(Reader reader) throws Exception {
        try (BufferedReader bufferedReader = toBufferedReader(reader)) {
            return parseInternal(bufferedReader);
        }
    }

    // Convenience - InputStream with default encoding
    public Parsefy<T> parse(InputStream inputStream) throws Exception {
        return parse(inputStream, defaultCharset);
    }

    // Convenience - InputStream with explicit encoding
    public Parsefy<T> parse(InputStream inputStream, Charset charset) throws Exception {
        return parse(new InputStreamReader(inputStream, charset));
    }

    // Convenience - File with default encoding
    public Parsefy<T> parse(File file) throws Exception {
        return parse(file, defaultCharset);
    }

    // Convenience - File with explicit encoding
    public Parsefy<T> parse(File file, Charset charset) throws Exception {
        return parse(new FileReader(file, charset));
    }

    // Convenience - String
    public Parsefy<T> parse(String csvContent) throws Exception {
        return parse(new StringReader(csvContent));
    }

    // Convenience - Path (Java NIO)
    public Parsefy<T> parse(Path path) throws Exception {
        return parse(path, defaultCharset);
    }

    public Parsefy<T> parse(Path path, Charset charset) throws Exception {
        return parse(Files.newBufferedReader(path, charset));
    }

    public Parsefy<T> defaultCharset(Charset charset) {
        this.defaultCharset = charset;
        return this;
    }

    private BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader
                ? (BufferedReader) reader
                : new BufferedReader(reader);
    }

    private Parsefy<T> parseInternal(Reader reader) throws Exception {
        List<T> validRows = new ArrayList<>();
        List<RowError> errorRows = new ArrayList<>();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(this.delimiter)
                .setTrim(this.trim)
                .setHeader()
                .get();

        CSVParser parser = CSVParser.parse(reader, csvFormat);
        List<CSVRecord> records = parser.getRecords();

        SchemaDefinition<T> schema = SchemaReader.read(schemaClass);

        for (CSVRecord record : records) {
            T instance = convertToObject(schema, record);
            ValidationResult result = validateRecord(instance, schema, record);
            if (result.isValid()) {
                validRows.add(instance);
            } else {
                if (strictMode) {
                    throw new RuntimeException(result.getErrors().get(0));
                } else {
                    errorRows.add(new RowError(record.getRecordNumber(), "validation", result.getErrors()));
                }
            }
        }
        this.result.setErrors(errorRows);
        this.result.setValidRows(validRows);
        return this;
    }

    public ParsefyResult<T> getResult() {
        return this.result;
    }

    private ValidationResult validateRecord(T instance, SchemaDefinition<T> schema, CSVRecord record) throws Exception {
        ValidationResult result = new ValidationResult();
        for (FieldDefinition fieldDef : schema.getFields()) {
            Object value = fieldDef.getValue(instance);
            List<FieldValidator<?>> validators = fieldDef.getValidators();
            for (FieldValidator<?> validator : validators) {
                ValidationResult fieldResult = ((FieldValidator<Object>) validator).validate(value);
                if (shouldIgnoreValidationError(fieldDef, fieldResult, record)) continue;
                result.merge(fieldResult);
            }
        }
        return result;
    }

    private boolean shouldIgnoreValidationError(
            FieldDefinition fieldDef,
            ValidationResult fieldResult,
            CSVRecord record) {
        // Ignore validation errors for optional fields that don't exist in the CSV
        boolean isOptionalField = !fieldDef.isRequired();
        boolean hasValidationError = !fieldResult.isValid();
        boolean columnNotInCsv = !record.isMapped(fieldDef.getColumnName());

        return isOptionalField && hasValidationError && columnNotInCsv;
    }

    private T convertToObject(SchemaDefinition<T> schema, CSVRecord record) throws Exception {
        T instance = schema.getClazz().getDeclaredConstructor().newInstance();
        for (FieldDefinition fieldDefinition : schema.getFields()) {
            if (record.isMapped(fieldDefinition.getColumnName())) {
                Object value = TypeConverter.convert(record.get(fieldDefinition.getColumnName()), fieldDefinition.getField().getType(), fieldDefinition.getField());
                fieldDefinition.setValue(instance, value);
            } else if (fieldDefinition.isRequired()) {
                throw new RuntimeException("Required field '" + fieldDefinition.getColumnName() + "' is missing");
            }
        }
        return instance;
    }
}

package com.haiphamcoder.cdp.shared.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class CSVFileUtils {
    private static final char DEFAULT_COLUMN_SEPARATOR = ',';

    public static List<String> getFieldNames(InputStream inputStream) {
        return getFieldNames(inputStream, Charset.defaultCharset(), DEFAULT_COLUMN_SEPARATOR);
    }

    public static List<String> getFieldNames(InputStream inputStream, Charset charset, char columnSeparator) {
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, charset))
                .withSkipLines(1)
                .withCSVParser(new CSVParserBuilder().withSeparator(columnSeparator).build())
                .build()) {
            String[] fieldNames = reader.readNext();
            return fieldNames != null ? Arrays.asList(fieldNames) : Collections.emptyList();
        } catch (IOException | CsvValidationException e) {
            log.error("Failed to read CSV field names", e);
            throw new RuntimeException("Failed to read CSV field names", e);
        }
    }

    public static List<String[]> getRecords(InputStream inputStream, int skip, int limit) {
        return getRecords(inputStream, skip, limit, Charset.defaultCharset(), DEFAULT_COLUMN_SEPARATOR);
    }

    public static List<String[]> getAllRecords(InputStream inputStream) {
        return getAllRecords(inputStream, Charset.defaultCharset(), DEFAULT_COLUMN_SEPARATOR);
    }

    public static List<String[]> getAllRecords(InputStream inputStream, Charset charset) {
        return getAllRecords(inputStream, charset, DEFAULT_COLUMN_SEPARATOR);
    }

    public static List<String[]> getAllRecords(InputStream inputStream, Charset charset, char columnSeparator) {
        return getRecords(inputStream, 0, Integer.MAX_VALUE, charset, columnSeparator);
    }

    public static List<String[]> getRecords(InputStream inputStream, int skip, int limit, Charset charset,
            char columnSeparator) {
        List<String[]> records = new LinkedList<>();
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, charset))
                .withSkipLines(skip + 1)
                .withCSVParser(new CSVParserBuilder().withSeparator(columnSeparator).build())
                .build()) {
            while (records.size() < limit) {
                String[] record = reader.readNext();
                if (record == null) {
                    break;
                }
                records.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            log.error("Failed to read CSV records", e);
            throw new RuntimeException("Failed to read CSV records", e);
        }
        return records;
    }

    public static long countRecords(InputStream inputStream) {
        return countRecords(inputStream, Charset.defaultCharset(), DEFAULT_COLUMN_SEPARATOR, true);
    }

    public static long countRecords(InputStream inputStream, Charset charset) {
        return countRecords(inputStream, charset, DEFAULT_COLUMN_SEPARATOR, true);
    }

    public static long countRecords(InputStream inputStream, Charset charset, char columnSeparator) {
        return countRecords(inputStream, charset, columnSeparator, true);
    }

    public static long countRecords(InputStream inputStream, Charset charset, char columnSeparator, boolean skipHeader) {
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, charset))
                .withCSVParser(new com.opencsv.CSVParserBuilder().withSeparator(columnSeparator).build())
                .withSkipLines(skipHeader ? 1 : 0)
                .build()) {
            return reader.readAll().stream().count();
        } catch (IOException | CsvException e) {
            log.error("Failed to count CSV records", e);
            throw new RuntimeException("Failed to count CSV records", e);
        }
    }
}

package com.haiphamcoder.cdp.shared.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.opencsv.CSVParser;
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

    private static final char[] COMMON_DELIMITERS = { ',', ';', '\t', '|'};
    private static final char DEFAULT_COLUMN_SEPARATOR = ',';

    public static char detectDelimiter(String line) {
        char bestDelimiter = '\0';

        // char bestDelimiter = DEFAULT_COLUMN_SEPARATOR;
        int maxFields = 0;

        // Try each common delimiter and pick the one that splits into the most fields
        for (char delimiter : COMMON_DELIMITERS) {
            if (!line.contains(String.valueOf(delimiter))) {
                continue;
            }
            CSVParser parser = new CSVParserBuilder().withSeparator(delimiter).build();
            String[] fields;
            try {
                fields = parser.parseLine(line);
                if (fields.length > maxFields) {
                    maxFields = fields.length;
                    bestDelimiter = delimiter;
                }
            } catch (IOException e) {
                log.error("Error parsing line with delimiter '{}': {}", delimiter, e.getMessage());
            }
        }

        return bestDelimiter;
    }

    /**
     * Get field names from CSV file
     * 
     * @param inputStream input stream of CSV file
     * @return list of field names
     */
    public static List<String> getFieldNames(InputStream inputStream) {
        return getFieldNames(inputStream, Charset.defaultCharset());
    }

    /**
     * Get field names from CSV file
     * 
     * @param inputStream input stream of CSV file
     * @param charset     charset of CSV file
     * @return list of field names
     */
    public static List<String> getFieldNames(InputStream inputStream, Charset charset) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, charset))) {
            String[] fieldNames = reader.readNext();
            return Arrays.asList(fieldNames);
        } catch (IOException | CsvValidationException e) {
            log.error("Failed to read CSV field names", e);
            throw new RuntimeException("Failed to read CSV field names", e);
        }
    }

    /**
     * Get records from CSV file
     * 
     * @param inputStream input stream of CSV file
     * @param skip        number of lines to skip
     * @param limit       number of records to read
     * @return list of records
     */
    public static List<String[]> getRecords(InputStream inputStream, int skip, int limit) {
        return getRecords(inputStream, skip, limit, Charset.defaultCharset());
    }

    /**
     * Get records from CSV file
     * 
     * @param inputStream input stream of CSV file
     * @param skip        number of lines to skip
     * @param limit       number of records to read
     * @param charset     charset of CSV file
     * @return list of records
     */
    public static List<String[]> getRecords(InputStream inputStream, int skip, int limit, Charset charset) {
        List<String[]> records = new LinkedList<>();
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, charset))
                .withSkipLines(skip + 1)
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

    /**
     * Count records from CSV file
     * 
     * @param inputStream input stream of CSV file
     * @return number of records
     */
    public static long countRecords(InputStream inputStream) {
        return countRecords(inputStream, Charset.defaultCharset(), DEFAULT_COLUMN_SEPARATOR, true);
    }

    /**
     * Count records from CSV file
     * 
     * @param inputStream input stream of CSV file
     * @param charset     charset of CSV file
     * @return number of records
     */
    public static long countRecords(InputStream inputStream, Charset charset) {
        return countRecords(inputStream, charset, DEFAULT_COLUMN_SEPARATOR, true);
    }

    /**
     * Count records from CSV file
     * 
     * @param inputStream     input stream of CSV file
     * @param charset         charset of CSV file
     * @param columnSeparator column separator of CSV file
     * @return number of records
     */
    public static long countRecords(InputStream inputStream, Charset charset, char columnSeparator) {
        return countRecords(inputStream, charset, columnSeparator, true);
    }

    /**
     * Count records from CSV file
     * 
     * @param inputStream     input stream of CSV file
     * @param charset         charset of CSV file
     * @param columnSeparator column separator of CSV file
     * @param skipHeader      skip header of CSV file
     * @return number of records
     */
    public static long countRecords(InputStream inputStream, Charset charset, char columnSeparator,
            boolean skipHeader) {
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

    /**
     * Create CSV reader
     * 
     * @param inputStream input stream of CSV file
     * @return CSV reader
     */
    public static CSVReader createCSVReader(InputStream inputStream) {
        return createCSVReader(inputStream, Charset.defaultCharset(), DEFAULT_COLUMN_SEPARATOR, 0);
    }

    /**
     * Create CSV reader
     * 
     * @param inputStream input stream of CSV file
     * @param charset     charset of CSV file
     * @return CSV reader
     */
    public static CSVReader createCSVReader(InputStream inputStream, Charset charset) {
        return createCSVReader(inputStream, charset, DEFAULT_COLUMN_SEPARATOR, 0);
    }

    /**
     * Create CSV reader
     * 
     * @param inputStream     input stream of CSV file
     * @param columnSeparator column separator of CSV file
     * @return CSV reader
     */
    public static CSVReader createCSVReader(InputStream inputStream, char columnSeparator) {
        return createCSVReader(inputStream, Charset.defaultCharset(), columnSeparator, 0);
    }

    /**
     * Create CSV reader
     * 
     * @param inputStream     input stream of CSV file
     * @param charset         charset of CSV file
     * @param columnSeparator column separator of CSV file
     * @param skipLines       number of lines to skip
     * @return CSV reader
     */
    public static CSVReader createCSVReader(InputStream inputStream, Charset charset, char columnSeparator,
            int skipLines) {
        return new CSVReaderBuilder(new InputStreamReader(inputStream, charset))
                .withSkipLines(skipLines)
                .withCSVParser(new CSVParserBuilder().withSeparator(columnSeparator).build())
                .build();
    }
}

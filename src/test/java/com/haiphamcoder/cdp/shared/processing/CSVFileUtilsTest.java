package com.haiphamcoder.cdp.shared.processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVFileUtilsTest {

    @Test
    public void testGetHeader() {
        InputStream inputStream = getClass().getResourceAsStream("/csv/test-input.csv");
        List<String> header = CSVFileUtils.getHeader(inputStream);
        log.info("Header: {}", header);
    }

    @Test
    public void testGetRecords() {
        InputStream inputStream = getClass().getResourceAsStream("/csv/test-input.csv");
        Stream<List<?>> records = CSVFileUtils.getRecords(inputStream);
        records.forEach(record -> {
            log.info("Record: {}", record);
        });
    }

    @Test
    public void testGetRecordsWithSkipAndLimit() {
        InputStream inputStream = getClass().getResourceAsStream("/csv/test-input.csv");
        Stream<List<?>> records = CSVFileUtils.getRecords(inputStream, 1, 2);
        records.forEach(record -> {
            log.info("Record: {}", record);
        });
    }

    @Test
    public void testGetRecordsWithCSVReader() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/csv/test-input.csv")))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1000).build();
            List<String[]> records = csvReader.readAll();
            records.forEach(record -> {
                log.info("Record: {}", Arrays.toString(record));
            });
        } catch (IOException | CsvException e) {
            log.error("Error reading CSV file", e);
        }
    }
}

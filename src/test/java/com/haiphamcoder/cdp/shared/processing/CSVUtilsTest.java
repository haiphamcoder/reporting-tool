package com.haiphamcoder.cdp.shared.processing;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class CSVFileUtilsTest {

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

} 

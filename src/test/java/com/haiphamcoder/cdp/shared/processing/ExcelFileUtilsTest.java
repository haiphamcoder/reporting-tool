package com.haiphamcoder.cdp.shared.processing;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelFileUtilsTest {

    @Test
    public void testGetHeader() {
        InputStream inputStream = getClass().getResourceAsStream("/excel/test-input.xls");
        List<String> header = ExcelFileUtils.getHeader(inputStream);
        log.info("Header: {}", header);
    }

    @Test
    public void testGetRecords() {
        InputStream inputStream = getClass().getResourceAsStream("/excel/test-input.xls");
        Stream<List<?>> records = ExcelFileUtils.getRecords(inputStream);
        records.forEach(record -> {
            log.info("Record: {}", record);
        });
    }

}

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
        List<String> header = ExcelFileUtils.getHeader(inputStream, "test-input.xls");
        log.info("Header: {}", header);
    }

    @Test
    public void testGetRecords() {
        InputStream inputStream = getClass().getResourceAsStream("/excel/test-input.xls");
        Stream<List<?>> records = ExcelFileUtils.getRecords(inputStream, "test-input.xls");
        records.forEach(record -> {
            log.info("Record: {}", record);
        });
    }

    @Test
    public void testGetHeaderXlsx() {
        InputStream inputStream = getClass().getResourceAsStream("/excel/uae_used_cars_10k.xlsx");
        List<String> header = ExcelFileUtils.getHeader(inputStream, "uae_used_cars_10k.xlsx");
        log.info("Header: {}", header);
    }

}

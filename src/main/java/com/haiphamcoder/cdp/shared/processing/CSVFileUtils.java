package com.haiphamcoder.cdp.shared.processing;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Streams;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.collect.ImmutableList.toImmutableList;

@UtilityClass
@Slf4j
public class CSVFileUtils {
    private static final char DEFAULT_COLUMN_SEPARATOR = ',';
    private static final CsvMapper csvMapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY).enable(CsvParser.Feature.TRIM_SPACES);
    private static final CsvSchema csvSchema = CsvSchema.emptySchema().withColumnSeparator(DEFAULT_COLUMN_SEPARATOR);

    public static List<String> getHeader(InputStream inputStream){
        try {
            MappingIterator<List<String>> iterator = csvMapper.readerFor(List.class).with(csvSchema).readValues(inputStream);
            List<String> header = iterator.next();
            return header.stream().collect(toImmutableList());
        } catch (IOException e) {
            log.error("Failed to read CSV header", e);
            throw new RuntimeException("Failed to read CSV header", e);
        }
    }

    public static Stream<List<?>> getRecords(InputStream inputStream) {
        try {
            MappingIterator<List<?>> iterator = csvMapper.readerFor(List.class).with(csvSchema).readValues(inputStream);
            return Streams.stream(iterator).skip(1);
        } catch (IOException e) {
            log.error("Failed to read CSV records", e);
            throw new RuntimeException("Failed to read CSV records", e);
        }
    }
    
}                       
    

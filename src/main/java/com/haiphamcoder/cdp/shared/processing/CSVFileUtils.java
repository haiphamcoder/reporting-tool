package com.haiphamcoder.cdp.shared.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
    private static final CsvMapper csvMapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY)
            .enable(CsvParser.Feature.TRIM_SPACES);
    private static final CsvSchema csvSchema = CsvSchema.emptySchema().withColumnSeparator(DEFAULT_COLUMN_SEPARATOR);

    public static List<String> getHeader(InputStream inputStream) {
        return getHeader(inputStream, Charset.defaultCharset());
    }

    public static List<String> getHeader(InputStream inputStream, Charset charset) {
        try {
            MappingIterator<List<String>> iterator = csvMapper.readerFor(List.class)
                    .with(csvSchema)
                    .readValues(new InputStreamReader(inputStream, charset));
            List<String> header = iterator.next();
            return header.stream().collect(toImmutableList());
        } catch (IOException e) {
            log.error("Failed to read CSV header", e);
            throw new RuntimeException("Failed to read CSV header", e);
        }
    }

    public static Stream<List<?>> getRecords(InputStream inputStream) {
        return getRecords(inputStream, Charset.defaultCharset());
    }

    public static Stream<List<?>> getRecords(InputStream inputStream, Charset charset) {
        try {
            MappingIterator<List<?>> iterator = csvMapper.readerFor(List.class)
                    .with(csvSchema)
                    .readValues(new InputStreamReader(inputStream, charset));
            return Streams.stream(iterator).skip(1);
        } catch (IOException e) {
            log.error("Failed to read CSV records", e);
            throw new RuntimeException("Failed to read CSV records", e);
        }
    }

    public static Stream<List<?>> getRecords(InputStream inputStream, int skip, int limit) {
        return getRecords(inputStream, skip, limit, Charset.defaultCharset());
    }

    public static Stream<List<?>> getRecords(InputStream inputStream, int skip, int limit, Charset charset) {
        try {
            MappingIterator<List<?>> iterator = csvMapper.readerFor(List.class)
                    .with(csvSchema)
                    .readValues(new InputStreamReader(inputStream, charset));
            return Streams.stream(iterator).skip(skip + 1).limit(limit);
        } catch (IOException e) {
            log.error("Failed to read CSV records", e);
            throw new RuntimeException("Failed to read CSV records", e);
        }
    }
}

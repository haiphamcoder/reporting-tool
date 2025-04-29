package com.haiphamcoder.cdp.shared.processing;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Streams;
import com.haiphamcoder.cdp.shared.MapperUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class JsonFileUtils {

    public static List<String> getHeader(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.mark(1024 * 1024); // 1MB
            String firstLine = reader.readLine();
            JsonNode node;
            try {
                node = MapperUtils.objectMapper.readTree(firstLine);
            } catch (JsonProcessingException e) {
                reader.reset();
                JsonNode root = MapperUtils.objectMapper.readTree(reader);
                Iterator<JsonNode> elements = root.elements();
                if (!elements.hasNext()) {
                    return List.of();
                }
                node = elements.next();
            }
            return Streams.stream(node.fields())
                    .map(entry -> entry.getKey())
                    .collect(toImmutableList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Stream<List<?>> getRecords(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.mark(1024 * 1024); // 1MB
            String firstLine = reader.readLine();
            try {
                // Try to parse as single-line JSON
                JsonNode node = MapperUtils.objectMapper.readTree(firstLine);
                reader.reset();
                return Stream.of(Streams.stream(node.fields())
                        .map(entry -> entry.getValue().asText())
                        .collect(toImmutableList()));
            } catch (JsonProcessingException e) {
                // If single-line parsing fails, try multi-line JSON
                reader.reset();
                JsonNode root = MapperUtils.objectMapper.readTree(reader);
                return Streams.stream(root.elements())
                        .map(node -> Streams.stream(node.fields())
                                .map(entry -> entry.getValue().asText())
                                .collect(toImmutableList()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

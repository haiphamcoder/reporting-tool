package com.haiphamcoder.storage.shared.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.storage.shared.MapperUtils;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonNodeStringConverter implements AttributeConverter<JsonNode, String> {

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        if (attribute == null) return null;
        try {
            return attribute.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        JsonNode data = null;
        try {
            data = MapperUtils.objectMapper.readTree(dbData);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return data;
    }
}

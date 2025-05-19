package com.haiphamcoder.storage.shared.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.haiphamcoder.storage.shared.MapperUtils;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class MapStringConverter implements AttributeConverter<Map<String, Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) return null;
        try {
            return MapperUtils.objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("Error converting Map to String: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return MapperUtils.objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error converting String to Map: {}", e.getMessage(), e);
            return null;
        }
    }
}

package com.haiphamcoder.reporting.shared.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.haiphamcoder.reporting.shared.MapperUtils;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return MapperUtils.objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("Error converting Map to String: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return Collections.emptyList();
        }
        try {
            return MapperUtils.objectMapper.readValue(dbData, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            log.error("Error converting String to Map: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

}

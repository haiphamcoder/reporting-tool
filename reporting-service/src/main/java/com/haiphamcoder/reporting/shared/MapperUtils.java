package com.haiphamcoder.reporting.shared;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();
}

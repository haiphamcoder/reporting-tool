package com.haiphamcoder.reporting.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum định nghĩa các mode của chart
 */
public enum QueryType {
    BASIC("basic", "Basic Query"),
    ADVANCED("advanced", "Advanced Query");

    private final String value;
    private final String description;

    QueryType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static QueryType fromValue(String value) {
        for (QueryType mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown chart mode: " + value);
    }
} 
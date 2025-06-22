package com.haiphamcoder.reporting.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChartType {
    BAR("bar", "Bar Chart"),
    PIE("pie", "Pie Chart"),
    LINE("line", "Line Chart"),
    AREA("area", "Area Chart"),
    TABLE("table", "Data Table");

    private final String value;
    private final String displayName;

    ChartType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ChartType fromValue(String value) {
        for (ChartType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown chart type: " + value);
    }
}
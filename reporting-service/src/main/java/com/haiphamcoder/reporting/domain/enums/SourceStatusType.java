package com.haiphamcoder.reporting.domain.enums;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SourceStatusType {
    INIT(1, "init"),
    PREPARED(2, "prepared"),
    PROCESSING(3, "processing"),
    READY(4, "ready"),
    FAILED(-1, "failed");

    @JsonValue
    @Getter
    private final int value;

    @Getter
    private final String name;

    public static SourceStatusType fromValue(int value) {
        return Arrays.stream(SourceStatusType.values())
                .filter(status -> status.value == value)
                .findFirst()
                .orElse(null);
    }

    public static SourceStatusType fromName(String name) {
        return Arrays.stream(SourceStatusType.values())
                .filter(status -> status.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    public static List<SourceStatusType> getAllStatus() {
        return Arrays.asList(SourceStatusType.values());
    }
}

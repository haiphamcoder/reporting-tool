package com.haiphamcoder.reporting.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SourcePermissionType {
    VIEW("view"),
    EDIT("edit");

    @JsonProperty("value")
    @JsonValue
    @Getter
    private final String value;

    public static SourcePermissionType fromValue(String value) {
        return Arrays.stream(SourcePermissionType.values())
                .filter(permission -> permission.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}

package com.haiphamcoder.reporting.domain.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReportPermissionType {
    READ("r--"),
    WRITE("-w-"),
    READ_WRITE("rw-"),
    EXECUTE("--x"),
    READ_EXECUTE("r-x"),
    WRITE_EXECUTE("-wx"),
    ALL("rwx");

    @JsonProperty("value")
    @JsonValue
    @Getter
    private final String value;

    public static ReportPermissionType fromValue(String value) {
        return Arrays.stream(ReportPermissionType.values())
                .filter(permission -> permission.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}

package com.haiphamcoder.reporting.domain.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChartPermissionComposeKey implements Serializable {

    @Column(name = "chart_id", nullable = false)
    @JsonProperty("chart_id")
    private Long chartId;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChartPermissionComposeKey that = (ChartPermissionComposeKey) o;
        return Objects.equals(chartId, that.chartId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chartId, userId);
    }
}
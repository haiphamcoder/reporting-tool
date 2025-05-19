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
public class ReportPermissionComposeKey implements Serializable {

    @Column(name = "report_id", nullable = false)
    @JsonProperty("report_id")
    private Long reportId;

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
        ReportPermissionComposeKey that = (ReportPermissionComposeKey) o;
        return Objects.equals(reportId, that.reportId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, userId);
    }
}

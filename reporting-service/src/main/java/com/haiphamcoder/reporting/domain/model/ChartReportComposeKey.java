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
public class ChartReportComposeKey implements Serializable {

    @Column(name = "chart_id", nullable = false)
    @JsonProperty("chart_id")
    private Long chartId;

    @Column(name = "report_id", nullable = false)
    @JsonProperty("report_id")
    private Long reportId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChartReportComposeKey that = (ChartReportComposeKey) o;
        return Objects.equals(chartId, that.chartId) &&
                Objects.equals(reportId, that.reportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chartId, reportId);
    }
}

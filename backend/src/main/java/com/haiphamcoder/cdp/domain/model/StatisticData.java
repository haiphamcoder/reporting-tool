package com.haiphamcoder.cdp.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatisticData {

    @JsonProperty("source_statistic")
    private StatisticItem sourceStatistic;

    @JsonProperty("chart_statistic")
    private StatisticItem chartStatistic;

    @JsonProperty("report_statistic")
    private StatisticItem reportStatistic;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatisticItem {

        @JsonProperty("total")
        private Long total;

        @JsonProperty("trend")
        private String trend;

        @JsonProperty("data")
        private List<Long> data;
    }

}

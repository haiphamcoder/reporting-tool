package com.haiphamcoder.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregationConfig {
    private String function;      // COUNT, SUM, AVG, etc.
    private String field;         // Field to apply the aggregation on
    private String alias;         // Alias for the aggregated result
} 
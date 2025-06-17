package com.haiphamcoder.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedQueryConfig {
    private String customQuery;
    private boolean validateQuery; // Flag to indicate if the query should be validated before execution
} 
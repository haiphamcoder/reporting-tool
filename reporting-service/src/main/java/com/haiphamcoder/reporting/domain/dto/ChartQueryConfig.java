package com.haiphamcoder.reporting.domain.dto;

import com.haiphamcoder.reporting.domain.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartQueryConfig {
    private QueryType queryType;
    private BasicQueryConfig basicConfig;
    private AdvancedQueryConfig advancedConfig;
    
    // Validate that only one config type is set based on queryType
    public void validate() {
        if (queryType == QueryType.BASIC && basicConfig == null) {
            throw new IllegalArgumentException("Basic query config is required for BASIC query type");
        }
        if (queryType == QueryType.ADVANCED && advancedConfig == null) {
            throw new IllegalArgumentException("Advanced query config is required for ADVANCED query type");
        }
        if (queryType == QueryType.BASIC && advancedConfig != null) {
            throw new IllegalArgumentException("Advanced query config should not be set for BASIC query type");
        }
        if (queryType == QueryType.ADVANCED && basicConfig != null) {
            throw new IllegalArgumentException("Basic query config should not be set for ADVANCED query type");
        }
    }
} 
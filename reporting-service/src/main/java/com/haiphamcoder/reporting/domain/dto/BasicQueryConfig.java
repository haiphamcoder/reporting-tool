package com.haiphamcoder.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicQueryConfig {
    private List<TableConfig> tables;     // List of tables to query from
    private List<String> groupByFields;   // Fields to group by (can be from different tables)
    private List<String> orderByFields;   // Fields to order by (can be from different tables)
    private String orderDirection;        // ASC or DESC
    private Integer limit;                // Limit the number of results
    private List<AggregationConfig> aggregations;    // List of aggregation functions

    // Helper method to validate the configuration
    public void validate() {
        if (tables == null || tables.isEmpty()) {
            throw new IllegalArgumentException("At least one table configuration is required");
        }
        
        // Validate that all referenced table aliases in joins exist
        for (TableConfig table : tables) {
            if (table.getJoins() != null) {
                for (JoinConfig join : table.getJoins()) {
                    boolean targetAliasExists = tables.stream()
                        .anyMatch(t -> join.getTargetTableAlias().equals(t.getAlias()));
                    if (!targetAliasExists) {
                        throw new IllegalArgumentException(
                            "Join target table alias " + join.getTargetTableAlias() + " not found in table configurations");
                    }
                }
            }
        }
    }
}
package com.haiphamcoder.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableConfig {
    private Long sourceId;           // Reference to the source ID
    private String tableName;        // Name of the table (from source's tableName)
    private String alias;            // Alias for the table in the query
    private List<String> selectedFields; // Fields to select from this table (based on source's mapping)
    private List<WhereClause> whereClauses; // Where conditions specific to this table
    private List<JoinConfig> joins;    // Join configurations with other tables
} 
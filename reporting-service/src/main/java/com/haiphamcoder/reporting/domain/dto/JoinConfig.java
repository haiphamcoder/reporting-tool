package com.haiphamcoder.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinConfig {
    private String joinType;        // INNER, LEFT, RIGHT, FULL
    private String targetTableAlias; // Alias of the table to join with
    private String sourceField;     // Field from the source table
    private String targetField;     // Field from the target table
    private List<WhereClause> joinConditions; // Additional join conditions
} 
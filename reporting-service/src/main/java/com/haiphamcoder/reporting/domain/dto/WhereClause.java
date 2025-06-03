package com.haiphamcoder.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhereClause {
    private String field;
    private String operator; // =, >, <, >=, <=, LIKE, IN, etc.
    private Object value;
} 
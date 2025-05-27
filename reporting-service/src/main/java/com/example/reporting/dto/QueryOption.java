package com.example.reporting.dto;

import lombok.Data;
import java.util.List;

@Data
public class QueryOption {
    private List<TableQuery> tables;
    private List<JoinCondition> joins;
    private List<String> selectFields;
    private List<FilterCondition> filters;
    private List<AggregationFunction> aggregations;
    private List<String> groupBy;
    private List<OrderBy> orderBy;
    private Integer limit;
    private Integer offset;

    @Data
    public static class TableQuery {
        private String name;
        private String alias;
    }

    @Data
    public static class JoinCondition {
        private String leftTable;
        private String rightTable;
        private String leftField;
        private String rightField;
        private String type; // INNER, LEFT, RIGHT, FULL
    }

    @Data
    public static class FilterCondition {
        private String field;
        private String operator; // =, >, <, >=, <=, LIKE, IN, etc.
        private Object value;
    }

    @Data
    public static class AggregationFunction {
        private String function; // SUM, AVG, COUNT, MIN, MAX
        private String field;
        private String alias;
    }

    @Data
    public static class OrderBy {
        private String field;
        private String direction; // ASC, DESC
    }
} 
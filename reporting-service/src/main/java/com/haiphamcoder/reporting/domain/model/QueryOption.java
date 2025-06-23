package com.haiphamcoder.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryOption {

    @JsonProperty("table")
    private String table;

    @JsonProperty("fields")
    private List<Field> fields;

    @JsonProperty("filters")
    private List<Filter> filters;

    @JsonProperty("sort")
    private List<Sort> sort;

    @JsonProperty("pagination")
    private Pagination pagination;

    @JsonProperty("group_by")
    private List<String> groupBy;

    @JsonProperty("aggregations")
    private List<Aggregation> aggregations;

    @JsonProperty("joins")
    private List<Join> joins;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field {
        @JsonProperty("field_name")
        private String fieldName;

        @JsonProperty("data_type")
        private String dataType;

        @JsonProperty("alias")
        private String alias;

        @JsonProperty("source_id")
        private String sourceId;

        @JsonProperty("source_name")
        private String sourceName;

        @JsonProperty("field_mapping")
        private String fieldMapping;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Filter {
        @JsonProperty("field")
        private String field;

        @JsonProperty("operator")
        private String operator;

        @JsonProperty("value")
        private Object value;

        @JsonProperty("data_type")
        private String dataType;

        @JsonProperty("source_id")
        private String sourceId;

        @JsonProperty("source_name")
        private String sourceName;

        @JsonProperty("field_mapping")
        private String fieldMapping;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Sort {
        @JsonProperty("field")
        private String field;

        @JsonProperty("direction")
        private String direction; // ASC or DESC
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pagination {
        @JsonProperty("page")
        private Integer page;

        @JsonProperty("size")
        private Integer size;

        @JsonProperty("total")
        private Long total;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Aggregation {
        @JsonProperty("field")
        private String field;

        @JsonProperty("function")
        private String function; // SUM, AVG, COUNT, MIN, MAX

        @JsonProperty("alias")
        private String alias;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Join {
        @JsonProperty("table")
        private String table;

        @JsonProperty("type")
        private String type; // INNER, LEFT, RIGHT, FULL

        @JsonProperty("conditions")
        private List<JoinCondition> conditions;

        @JsonProperty("alias")
        private String alias;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JoinCondition {

        @JsonProperty("left_source_id")
        private String leftSourceId;

        @JsonProperty("right_source_id")
        private String rightSourceId;

        @JsonProperty("left_field")
        private String leftField;

        @JsonProperty("right_field")
        private String rightField;

        @JsonProperty("operator")
        private String operator; // EQ, GT, GTE, LT, LTE
    }

}

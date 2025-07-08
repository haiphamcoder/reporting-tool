package com.haiphamcoder.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Arrays;

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
    private FilterNode filters;

    @JsonProperty("sort")
    private List<Sort> sort;

    @JsonProperty("group_by")
    private List<String> groupBy;

    @JsonProperty("having")
    private List<Field> having;

    @JsonProperty("joins")
    private List<Join> joins;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field {

        @JsonProperty("field")
        private String field;

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

        @JsonProperty("field_type")
        private String fieldType;

        @JsonProperty("table_name")
        private String tableName;

        @JsonProperty("table_alias")
        private String tableAlias;

        @JsonProperty("function")
        private AggregateFunction function;

        @JsonProperty("prefix")
        private String prefix;

    }

    @AllArgsConstructor
    public static enum AggregateFunction {
        SUM("SUM"),
        AVG("AVG"),
        COUNT("COUNT"),
        MIN("MIN"),
        MAX("MAX");

        @Getter
        @JsonValue
        private String value;

        public static AggregateFunction fromValue(String value) {
            return Arrays.stream(AggregateFunction.values())
                    .filter(aggregateFunction -> aggregateFunction.value.equals(value))
                    .findFirst()
                    .orElse(null);
        }
    }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    @JsonSubTypes({
        @JsonSubTypes.Type(value = FilterConditionNode.class, name = "condition"),
        @JsonSubTypes.Type(value = FilterGroupNode.class, name = "group")
    })
    public interface FilterNode {
        // Base interface for filter nodes
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FilterConditionNode implements FilterNode {

        @JsonProperty("type")
        @Builder.Default
        private FilterNodeType type = FilterNodeType.CONDITION;

        @JsonProperty("id")
        private String id;

        @JsonProperty("operator")
        private FilterConditionOperator operator;

        @JsonProperty("value")
        private Object value;

        @JsonProperty("source_field")
        private Field sourceField;

        @JsonProperty("target_field")
        private Field targetField;

        @JsonProperty("compare_with_other_field")
        private Boolean compareWithOtherField;
 
    }

    @AllArgsConstructor
    public static enum FilterConditionOperator {
        EQ("EQ"),
        NE("NE"),
        GT("GT"),
        GTE("GTE"),
        LT("LT"),
        LTE("LTE"),
        IN("IN"),
        NOT_IN("NOT IN"),
        BETWEEN("BETWEEN"),
        LIKE("LIKE"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL");

        @Getter
        @JsonValue
        private String value;

        public static FilterConditionOperator fromValue(String value) {
            return Arrays.stream(FilterConditionOperator.values())
                    .filter(filterConditionOperator -> filterConditionOperator.value.equals(value))
                    .findFirst()
                    .orElse(null);
        }
    }

    @AllArgsConstructor
    public static enum FilterNodeType {
        CONDITION("condition"),
        GROUP("group");

        @Getter
        @JsonValue
        private String value;

        public static FilterNodeType fromValue(String value) {
            return Arrays.stream(FilterNodeType.values())
                    .filter(filterNodeType -> filterNodeType.value.equals(value))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FilterGroupNode implements FilterNode {

        @JsonProperty("type")
        @Builder.Default
        private FilterNodeType type = FilterNodeType.GROUP;

        @JsonProperty("id")
        private String id;

        @JsonProperty("op")
        private GroupOperator operator;

        @JsonProperty("elements")
        private List<FilterNode> elements;

        @AllArgsConstructor
        public static enum GroupOperator {
            AND("AND"),
            OR("OR");

            @Getter
            @JsonValue
            private String value;

            public static GroupOperator fromValue(String value) {
                return Arrays.stream(GroupOperator.values())
                        .filter(groupOperator -> groupOperator.value.equals(value))
                        .findFirst()
                        .orElse(null);
            }
        }
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

        @JsonProperty("source_id")
        private String sourceId;

        @JsonProperty("source_name")
        private String sourceName;

        @JsonProperty("table_name")
        private String tableName;

        @JsonProperty("table_alias")
        private String tableAlias;

        @JsonProperty("direction")
        private SortDirection direction;

        @AllArgsConstructor
        public static enum SortDirection {
            ASC("ASC"),
            DESC("DESC");

            @Getter
            @JsonValue
            private String value;

            public static SortDirection fromValue(String value) {
                return Arrays.stream(SortDirection.values())
                        .filter(sortDirection -> sortDirection.value.equals(value))
                        .findFirst()
                        .orElse(null);
            }
        }
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

        @JsonProperty("table_name")
        private String tableName;

        @JsonProperty("table_alias")
        private String tableAlias;

        @JsonProperty("type")
        private JoinType type;

        @JsonProperty("conditions")
        private List<JoinCondition> conditions;

        @JsonProperty("alias")
        private String alias;
    }

    @AllArgsConstructor
    public static enum JoinType {
        INNER("INNER"),
        LEFT("LEFT"),
        RIGHT("RIGHT"),
        CROSS("CROSS"),
        NATURAL_LEFT("NATURAL LEFT"),
        NATURAL_RIGHT("NATURAL RIGHT");

        @Getter
        @JsonValue
        private String value;

        public static JoinType fromValue(String value) {
            return Arrays.stream(JoinType.values())
                    .filter(joinType -> joinType.value.equals(value))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JoinCondition {

        @JsonProperty("left_table")
        private String leftTable;

        @JsonProperty("left_table_name")
        private String leftTableName;

        @JsonProperty("left_table_alias")
        private String leftTableAlias;

        @JsonProperty("left_field")
        private String leftField;

        @JsonProperty("right_table")
        private String rightTable;

        @JsonProperty("right_table_name")
        private String rightTableName;

        @JsonProperty("right_table_alias")
        private String rightTableAlias;

        @JsonProperty("right_field")
        private String rightField;

        @JsonProperty("operator")
        private JoinOperator operator;
    }

    @AllArgsConstructor
    public static enum JoinOperator {
        EQ("EQ"),
        NE("NE"),
        GT("GT"),
        GTE("GTE"),
        LT("LT"),
        LTE("LTE");

        @Getter
        @JsonValue
        private String value;

        public static JoinOperator fromValue(String value) {
            return Arrays.stream(JoinOperator.values())
                    .filter(joinOperator -> joinOperator.value.equals(value))
                    .findFirst()
                    .orElse(null);
        }
    }

}

package com.haiphamcoder.reporting.shared;

import com.haiphamcoder.reporting.domain.model.QueryOption;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryOptionToSqlConverter {

    public static String convertToSql(QueryOption queryOption, String mainTableName, Map<String, String> sourceTableNames) {
        if (queryOption == null || sourceTableNames == null || sourceTableNames.isEmpty() || !StringUtils.hasText(mainTableName)) {
            throw new IllegalArgumentException("QueryOption and sourceTableNames must not be null or empty");
        }

        StringBuilder sql = new StringBuilder();
        
        // SELECT clause
        sql.append("SELECT ");
        if (queryOption.getFields() != null && !queryOption.getFields().isEmpty()) {
            sql.append(queryOption.getFields().stream()
                    .map(field -> {
                        String sourceId = field.getSourceId();
                        String tableName = sourceTableNames.get(sourceId);
                        String fieldMapping = field.getFieldMapping();
                        String alias = field.getAlias();
                        if (alias != null && !alias.isEmpty()) {
                            return String.format("%s.%s AS %s", tableName, fieldMapping, alias);
                        }
                        return String.format("%s.%s", tableName, fieldMapping);
                    })
                    .collect(Collectors.joining(", ")));
        } else {
            sql.append("*");
        }

        // FROM clause
        sql.append(" FROM ").append(mainTableName);

        // JOIN clauses
        if (queryOption.getJoins() != null && !queryOption.getJoins().isEmpty()) {
            for (QueryOption.Join join : queryOption.getJoins()) {
                sql.append(" ").append(join.getType()).append(" JOIN ")
                   .append(sourceTableNames.get(join.getTable()));

                if (join.getConditions() != null && !join.getConditions().isEmpty()) {
                    sql.append(" ON ");
                    List<String> conditions = new ArrayList<>();
                    for (QueryOption.JoinCondition condition : join.getConditions()) {
                        String joinCondition = buildJoinCondition(condition, sourceTableNames);
                        if (joinCondition != null) {
                            conditions.add(joinCondition);
                        }
                    }
                    sql.append(String.join(" AND ", conditions));
                }
            }
        }

        // WHERE clause
        if (queryOption.getFilters() != null && !queryOption.getFilters().isEmpty()) {
            sql.append(" WHERE ");
            List<String> conditions = new ArrayList<>();
            for (QueryOption.Filter filter : queryOption.getFilters()) {
                String condition = buildFilterCondition(filter, sourceTableNames);
                if (condition != null) {
                    conditions.add(condition);
                }
            }
            sql.append(String.join(" AND ", conditions));
        }

        // GROUP BY clause
        if (queryOption.getGroupBy() != null && !queryOption.getGroupBy().isEmpty()) {
            sql.append(" GROUP BY ")
               .append(String.join(", ", queryOption.getGroupBy()));
        }

        // HAVING clause (for aggregations)
        if (queryOption.getAggregations() != null && !queryOption.getAggregations().isEmpty()) {
            sql.append(" HAVING ");
            List<String> havingConditions = new ArrayList<>();
            for (QueryOption.Aggregation agg : queryOption.getAggregations()) {
                String havingCondition = buildAggregationCondition(agg);
                if (havingCondition != null) {
                    havingConditions.add(havingCondition);
                }
            }
            sql.append(String.join(" AND ", havingConditions));
        }

        // ORDER BY clause
        if (queryOption.getSort() != null && !queryOption.getSort().isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(queryOption.getSort().stream()
                    .map(sort -> String.format("%s %s", sort.getField(), sort.getDirection()))
                    .collect(Collectors.joining(", ")));
        }

        // LIMIT and OFFSET
        if (queryOption.getPagination() != null) {
            QueryOption.Pagination pagination = queryOption.getPagination();
            if (pagination.getSize() != null) {
                sql.append(" LIMIT ").append(pagination.getSize());
                if (pagination.getPage() != null && pagination.getPage() > 0) {
                    sql.append(" OFFSET ").append((pagination.getPage() - 1) * pagination.getSize());
                }
            }
        }

        return sql.toString();
    }

    private static String buildJoinCondition(QueryOption.JoinCondition condition, Map<String, String> sourceTableNames) {
        if (condition == null || !StringUtils.hasText(condition.getLeftField()) || 
            !StringUtils.hasText(condition.getRightField()) || 
            !StringUtils.hasText(condition.getOperator())) {
            return null;
        }

        String operator = condition.getOperator().toUpperCase();
        String leftField = condition.getLeftField();
        String rightField = condition.getRightField();
        String leftSourceId = condition.getLeftSourceId();
        String rightSourceId = condition.getRightSourceId();
        String leftTableName = sourceTableNames.get(leftSourceId);
        String rightTableName = sourceTableNames.get(rightSourceId);

        switch (operator) {
            case "EQ":
                return String.format("%s.%s = %s.%s", leftTableName, leftField, rightTableName, rightField);
            case "GT":
                return String.format("%s.%s > %s.%s", leftTableName, leftField, rightTableName, rightField);
            case "GTE":
                return String.format("%s.%s >= %s.%s", leftTableName, leftField, rightTableName, rightField);
            case "LT":
                return String.format("%s.%s < %s.%s", leftTableName, leftField, rightTableName, rightField);
            case "LTE":
                return String.format("%s.%s <= %s.%s", leftTableName, leftField, rightTableName, rightField);
            default:
                return null;
        }
    }

    private static String buildFilterCondition(QueryOption.Filter filter, Map<String, String> sourceTableNames) {
        if (filter == null || !StringUtils.hasText(filter.getField()) || 
            !StringUtils.hasText(filter.getOperator()) || filter.getValue() == null) {
            return null;
        }

        String operator = filter.getOperator().toUpperCase();
        Object value = filter.getValue();
        String field = filter.getField();
        String sourceId = filter.getSourceId();
        String tableName = sourceTableNames.get(sourceId);

        switch (operator) {
            case "EQ":
                return String.format("%s.%s = %s", tableName, field, formatValue(value));
            case "NEQ":
                return String.format("%s.%s != %s", tableName, field, formatValue(value));
            case "GT":
                return String.format("%s.%s > %s", tableName, field, formatValue(value));
            case "GTE":
                return String.format("%s.%s >= %s", tableName, field, formatValue(value));
            case "LT":
                return String.format("%s.%s < %s", tableName, field, formatValue(value));
            case "LTE":
                return String.format("%s.%s <= %s", tableName, field, formatValue(value));
            case "LIKE":
                return String.format("%s.%s LIKE %s", tableName, field, formatValue("%" + value + "%"));
            case "IN":
                if (value instanceof List) {
                    List<?> values = (List<?>) value;
                    return String.format("%s.%s IN (%s)", tableName, field, 
                        values.stream()
                            .map(QueryOptionToSqlConverter::formatValue)
                            .collect(Collectors.joining(", ")));
                }
                return null;
            default:
                return null;
        }
    }

    private static String buildAggregationCondition(QueryOption.Aggregation agg) {
        if (agg == null || !StringUtils.hasText(agg.getField()) || 
            !StringUtils.hasText(agg.getFunction())) {
            return null;
        }

        String function = agg.getFunction().toUpperCase();
        String field = agg.getField();
        String alias = agg.getAlias() != null ? agg.getAlias() : 
            String.format("%s_%s", function.toLowerCase(), field);

        return String.format("%s(%s) AS %s", function, field, alias);
    }

    private static String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value.toString().replace("'", "''") + "'";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        return "'" + value.toString() + "'";
    }
}
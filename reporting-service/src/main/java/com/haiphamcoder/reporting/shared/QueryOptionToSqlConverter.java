package com.haiphamcoder.reporting.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haiphamcoder.reporting.domain.model.QueryOption;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryOptionToSqlConverter {

    public static String convertToSql(QueryOption queryOption, String tableName) {
        if (queryOption == null || !StringUtils.hasText(tableName)) {
            throw new IllegalArgumentException("QueryOption and tableName must not be null or empty");
        }

        StringBuilder sql = new StringBuilder();
        
        // SELECT clause
        sql.append("SELECT ");
        if (queryOption.getFields() != null && !queryOption.getFields().isEmpty()) {
            sql.append(queryOption.getFields().stream()
                    .map(field -> {
                        String fieldName = field.getFieldName();
                        return field.getAlias() != null ? 
                            String.format("%s AS %s", fieldName, field.getAlias()) : 
                            fieldName;
                    })
                    .collect(Collectors.joining(", ")));
        } else {
            sql.append("*");
        }

        // FROM clause
        sql.append(" FROM ").append(tableName);

        // JOIN clauses
        if (queryOption.getJoins() != null && !queryOption.getJoins().isEmpty()) {
            for (QueryOption.Join join : queryOption.getJoins()) {
                sql.append(" ").append(join.getType()).append(" JOIN ")
                   .append(join.getTable());
                
                if (StringUtils.hasText(join.getAlias())) {
                    sql.append(" AS ").append(join.getAlias());
                }

                if (join.getConditions() != null && !join.getConditions().isEmpty()) {
                    sql.append(" ON ");
                    List<String> conditions = new ArrayList<>();
                    for (QueryOption.JoinCondition condition : join.getConditions()) {
                        String joinCondition = buildJoinCondition(condition);
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
                String condition = buildFilterCondition(filter);
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

    private static String buildJoinCondition(QueryOption.JoinCondition condition) {
        if (condition == null || !StringUtils.hasText(condition.getLeftField()) || 
            !StringUtils.hasText(condition.getRightField()) || 
            !StringUtils.hasText(condition.getOperator())) {
            return null;
        }

        String operator = condition.getOperator().toUpperCase();
        String leftField = condition.getLeftField();
        String rightField = condition.getRightField();

        switch (operator) {
            case "EQ":
                return String.format("%s = %s", leftField, rightField);
            case "GT":
                return String.format("%s > %s", leftField, rightField);
            case "GTE":
                return String.format("%s >= %s", leftField, rightField);
            case "LT":
                return String.format("%s < %s", leftField, rightField);
            case "LTE":
                return String.format("%s <= %s", leftField, rightField);
            default:
                return null;
        }
    }

    private static String buildFilterCondition(QueryOption.Filter filter) {
        if (filter == null || !StringUtils.hasText(filter.getField()) || 
            !StringUtils.hasText(filter.getOperator()) || filter.getValue() == null) {
            return null;
        }

        String operator = filter.getOperator().toUpperCase();
        Object value = filter.getValue();
        String field = filter.getField();

        switch (operator) {
            case "EQ":
                return String.format("%s = %s", field, formatValue(value));
            case "NEQ":
                return String.format("%s != %s", field, formatValue(value));
            case "GT":
                return String.format("%s > %s", field, formatValue(value));
            case "GTE":
                return String.format("%s >= %s", field, formatValue(value));
            case "LT":
                return String.format("%s < %s", field, formatValue(value));
            case "LTE":
                return String.format("%s <= %s", field, formatValue(value));
            case "LIKE":
                return String.format("%s LIKE %s", field, formatValue("%" + value + "%"));
            case "IN":
                if (value instanceof List) {
                    List<?> values = (List<?>) value;
                    return String.format("%s IN (%s)", field, 
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

    public static void main(String[] args) throws JsonProcessingException {
        // Test case 1: Basic query with fields and filters
        QueryOption basicQuery = QueryOption.builder()
            .fields(Arrays.asList(
                new QueryOption.Field("id", "INTEGER", "user_id"),
                new QueryOption.Field("name", "VARCHAR", "user_name"),
                new QueryOption.Field("email", "VARCHAR", null)
            ))
            .filters(Arrays.asList(
                new QueryOption.Filter("status", "EQ", "active"),
                new QueryOption.Filter("age", "GT", 18)
            ))
            .build();
        String queryOptionString = MapperUtils.objectMapper.writeValueAsString(basicQuery);
        System.out.println(queryOptionString);
        System.out.println("Test Case 1 - Basic Query:");
        System.out.println(convertToSql(basicQuery, "users"));
        System.out.println("\n");

        // Test case 2: Query with joins
        QueryOption joinQuery = QueryOption.builder()
            .fields(Arrays.asList(
                new QueryOption.Field("u.id", "INTEGER", "user_id"),
                new QueryOption.Field("u.name", "VARCHAR", "user_name"),
                new QueryOption.Field("o.id", "INTEGER", "order_id"),
                new QueryOption.Field("o.amount", "DECIMAL", "order_amount")
            ))
            .joins(Arrays.asList(
                QueryOption.Join.builder()
                    .table("orders")
                    .type("LEFT")
                    .alias("o")
                    .conditions(Arrays.asList(
                        QueryOption.JoinCondition.builder()
                            .leftField("u.id")
                            .rightField("o.user_id")
                            .operator("EQ")
                            .build()
                    ))
                    .build()
            ))
            .filters(Arrays.asList(
                new QueryOption.Filter("u.status", "EQ", "active"),
                new QueryOption.Filter("o.created_at", "GT", "2024-01-01")
            ))
            .build();
        queryOptionString = MapperUtils.objectMapper.writeValueAsString(joinQuery);
        System.out.println(queryOptionString);
        System.out.println("Test Case 2 - Query with Joins:");
        System.out.println(convertToSql(joinQuery, "users u"));
        System.out.println("\n");

        // Test case 3: Query with group by and aggregations
        QueryOption aggregationQuery = QueryOption.builder()
            .fields(Arrays.asList(
                new QueryOption.Field("department", "VARCHAR", null),
                new QueryOption.Field("COUNT(*)", "INTEGER", "employee_count"),
                new QueryOption.Field("AVG(salary)", "DECIMAL", "avg_salary")
            ))
            .groupBy(Arrays.asList("department"))
            .aggregations(Arrays.asList(
                new QueryOption.Aggregation("salary", "AVG", "avg_salary"),
                new QueryOption.Aggregation("id", "COUNT", "employee_count")
            ))
            .filters(Arrays.asList(
                new QueryOption.Filter("status", "EQ", "active")
            ))
            .build();
        queryOptionString = MapperUtils.objectMapper.writeValueAsString(aggregationQuery);
        System.out.println(queryOptionString);
        System.out.println("Test Case 3 - Query with Group By and Aggregations:");
        System.out.println(convertToSql(aggregationQuery, "employees"));
        System.out.println("\n");

        // Test case 4: Query with pagination and sorting
        QueryOption paginationQuery = QueryOption.builder()
            .fields(Arrays.asList(
                new QueryOption.Field("id", "INTEGER", null),
                new QueryOption.Field("name", "VARCHAR", null),
                new QueryOption.Field("created_at", "TIMESTAMP", null)
            ))
            .sort(Arrays.asList(
                new QueryOption.Sort("created_at", "DESC"),
                new QueryOption.Sort("name", "ASC")
            ))
            .pagination(new QueryOption.Pagination(2, 10, null))
            .build();
        queryOptionString = MapperUtils.objectMapper.writeValueAsString(paginationQuery);
        System.out.println(queryOptionString);
        System.out.println("Test Case 4 - Query with Pagination and Sorting:");
        System.out.println(convertToSql(paginationQuery, "users"));
        System.out.println("\n");

        // Test case 5: Complex query with multiple joins and conditions
        QueryOption complexQuery = QueryOption.builder()
            .fields(Arrays.asList(
                new QueryOption.Field("u.id", "INTEGER", "user_id"),
                new QueryOption.Field("u.name", "VARCHAR", "user_name"),
                new QueryOption.Field("o.id", "INTEGER", "order_id"),
                new QueryOption.Field("p.name", "VARCHAR", "product_name"),
                new QueryOption.Field("c.name", "VARCHAR", "category_name")
            ))
            .joins(Arrays.asList(
                QueryOption.Join.builder()
                    .table("orders")
                    .type("LEFT")
                    .alias("o")
                    .conditions(Arrays.asList(
                        QueryOption.JoinCondition.builder()
                            .leftField("u.id")
                            .rightField("o.user_id")
                            .operator("EQ")
                            .build()
                    ))
                    .build(),
                QueryOption.Join.builder()
                    .table("products")
                    .type("INNER")
                    .alias("p")
                    .conditions(Arrays.asList(
                        QueryOption.JoinCondition.builder()
                            .leftField("o.product_id")
                            .rightField("p.id")
                            .operator("EQ")
                            .build()
                    ))
                    .build(),
                QueryOption.Join.builder()
                    .table("categories")
                    .type("LEFT")
                    .alias("c")
                    .conditions(Arrays.asList(
                        QueryOption.JoinCondition.builder()
                            .leftField("p.category_id")
                            .rightField("c.id")
                            .operator("EQ")
                            .build()
                    ))
                    .build()
            ))
            .filters(Arrays.asList(
                new QueryOption.Filter("u.status", "EQ", "active"),
                new QueryOption.Filter("o.created_at", "GT", "2024-01-01"),
                new QueryOption.Filter("p.price", "GT", 100),
                new QueryOption.Filter("c.name", "IN", Arrays.asList("Electronics", "Books"))
            ))
            .sort(Arrays.asList(
                new QueryOption.Sort("o.created_at", "DESC")
            ))
            .pagination(new QueryOption.Pagination(1, 20, null))
            .build();
        queryOptionString = MapperUtils.objectMapper.writeValueAsString(complexQuery);
        System.out.println(queryOptionString);
        System.out.println("Test Case 5 - Complex Query with Multiple Joins:");
        System.out.println(convertToSql(complexQuery, "users u"));
    }
}
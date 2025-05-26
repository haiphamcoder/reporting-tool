package com.example.reporting.service;

import com.example.reporting.dto.QueryOption;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public String buildQuery(QueryOption queryOption) {
        StringBuilder query = new StringBuilder();
        
        // SELECT clause
        query.append("SELECT ");
        List<String> selectParts = new ArrayList<>();
        
        // Add regular fields
        if (queryOption.getSelectFields() != null) {
            selectParts.addAll(queryOption.getSelectFields());
        }
        
        // Add aggregation functions
        if (queryOption.getAggregations() != null) {
            selectParts.addAll(queryOption.getAggregations().stream()
                .map(agg -> String.format("%s(%s) AS %s", 
                    agg.getFunction(), 
                    agg.getField(), 
                    agg.getAlias()))
                .collect(Collectors.toList()));
        }
        
        query.append(String.join(", ", selectParts));
        
        // FROM clause
        query.append(" FROM ");
        List<String> tableParts = queryOption.getTables().stream()
            .map(table -> String.format("%s %s", table.getName(), table.getAlias()))
            .collect(Collectors.toList());
        query.append(String.join(", ", tableParts));
        
        // JOIN clauses
        if (queryOption.getJoins() != null) {
            for (QueryOption.JoinCondition join : queryOption.getJoins()) {
                query.append(String.format(" %s JOIN %s ON %s.%s = %s.%s",
                    join.getType(),
                    join.getRightTable(),
                    join.getLeftTable(),
                    join.getLeftField(),
                    join.getRightTable(),
                    join.getRightField()));
            }
        }
        
        // WHERE clause
        if (queryOption.getFilters() != null && !queryOption.getFilters().isEmpty()) {
            query.append(" WHERE ");
            List<String> filterParts = queryOption.getFilters().stream()
                .map(filter -> String.format("%s %s ?", filter.getField(), filter.getOperator()))
                .collect(Collectors.toList());
            query.append(String.join(" AND ", filterParts));
        }
        
        // GROUP BY clause
        if (queryOption.getGroupBy() != null && !queryOption.getGroupBy().isEmpty()) {
            query.append(" GROUP BY ");
            query.append(String.join(", ", queryOption.getGroupBy()));
        }
        
        // ORDER BY clause
        if (queryOption.getOrderBy() != null && !queryOption.getOrderBy().isEmpty()) {
            query.append(" ORDER BY ");
            List<String> orderParts = queryOption.getOrderBy().stream()
                .map(order -> String.format("%s %s", order.getField(), order.getDirection()))
                .collect(Collectors.toList());
            query.append(String.join(", ", orderParts));
        }
        
        // LIMIT and OFFSET
        if (queryOption.getLimit() != null) {
            query.append(" LIMIT ").append(queryOption.getLimit());
        }
        if (queryOption.getOffset() != null) {
            query.append(" OFFSET ").append(queryOption.getOffset());
        }
        
        return query.toString();
    }

    @Transactional
    public void executeQueryAndStoreResults(String query, List<Object> params, String targetTable) {
        // Drop existing table if exists
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + targetTable);
        
        // Create new table with results
        String createTableQuery = "CREATE TABLE " + targetTable + " AS " + query;
        jdbcTemplate.execute(createTableQuery);
    }

    public QueryOption parseQueryOptions(String queryOptionsJson) {
        try {
            return objectMapper.readValue(queryOptionsJson, QueryOption.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse query options", e);
        }
    }
} 
package com.example.reporting.service;

import com.example.reporting.dto.ChartRequest;
import com.example.reporting.dto.QueryOption;
import com.example.reporting.model.Chart;
import com.example.reporting.repository.ChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChartService {
    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private QueryService queryService;

    @Transactional
    public Chart createChart(ChartRequest request) {
        Chart chart = new Chart();
        chart.setName(request.getName());
        chart.setType(request.getType());
        chart.setQueryOptions(request.getQueryOptions());
        chart.setDataTable("chart_data_" + UUID.randomUUID().toString().replace("-", ""));
        
        return chartRepository.save(chart);
    }

    public Chart getChart(Long id) {
        return chartRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Chart not found"));
    }

    @Transactional
    public void refreshChartData(Long id) {
        Chart chart = getChart(id);
        
        // Parse query options
        QueryOption queryOption = queryService.parseQueryOptions(chart.getQueryOptions());
        
        // Build SQL query
        String query = queryService.buildQuery(queryOption);
        
        // Extract parameters from filters
        List<Object> params = new ArrayList<>();
        if (queryOption.getFilters() != null) {
            for (QueryOption.FilterCondition filter : queryOption.getFilters()) {
                params.add(filter.getValue());
            }
        }
        
        // Execute query and store results
        queryService.executeQueryAndStoreResults(query, params, chart.getDataTable());
        
        // Update last refreshed time
        chart.setLastRefreshed(java.time.LocalDateTime.now());
        chartRepository.save(chart);
    }
} 
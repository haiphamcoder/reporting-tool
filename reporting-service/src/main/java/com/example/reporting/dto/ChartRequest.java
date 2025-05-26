package com.example.reporting.dto;

import lombok.Data;

@Data
public class ChartRequest {
    private String name;
    private String type;
    private String queryOptions;
} 
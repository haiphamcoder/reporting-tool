package com.haiphamcoder.dataprocessing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartSchedule {
    private Long chartId;
    private String cronExpression;
    private Boolean enabled;
    private String lastExecutionTime;
    private String nextExecutionTime;
    private String status;
}
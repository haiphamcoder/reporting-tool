package com.haiphamcoder.integrated.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceStatus {

    @JsonProperty("time")
    private long time;

    @JsonProperty("committed_virtual_memory_size")
    private long committedVirtualMemorySize;

    @JsonProperty("total_swap_space_size")
    private long totalSwapSpaceSize;

    @JsonProperty("free_swap_space_size")
    private long freeSwapSpaceSize;

    @JsonProperty("total_memory_size")
    private long totalMemorySize;

    @JsonProperty("free_memory_size")
    private long freeMemorySize;

    @JsonProperty("cpu_load")
    private double cpuLoad;

    @JsonProperty("process_cpu_load")
    private double processCpuLoad;
    
}

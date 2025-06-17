package com.haiphamcoder.dataprocessing.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.dataprocessing.domain.dto.ChartDto;
import com.haiphamcoder.dataprocessing.domain.dto.ChartDto.ChartDtoBuilder;
import com.haiphamcoder.dataprocessing.service.ChartGrpcClient;
import com.haiphamcoder.dataprocessing.shared.MapperUtils;
import com.haiphamcoder.dataprocessing.shared.StringUtils;
import com.haiphamcoder.reporting.proto.ChartProto;
import com.haiphamcoder.reporting.proto.ChartServiceGrpc;
import com.haiphamcoder.reporting.proto.GetChartByIdRequest;
import com.haiphamcoder.reporting.proto.GetChartByIdResponse;
import com.haiphamcoder.reporting.proto.UpdateChartRequest;
import com.haiphamcoder.reporting.proto.UpdateChartResponse;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChartGrpcClientImpl implements ChartGrpcClient {
    private final ChartServiceGrpc.ChartServiceBlockingStub chartServiceBlockingStub;

    public ChartGrpcClientImpl(@Qualifier("chartServiceChannel") ManagedChannel chartServiceChannel) {
        this.chartServiceBlockingStub = ChartServiceGrpc.newBlockingStub(chartServiceChannel);
    }

    @Override
    public ChartDto getChartById(Long id) {
        GetChartByIdRequest request = GetChartByIdRequest.newBuilder().setId(id).build();
        GetChartByIdResponse response = chartServiceBlockingStub.getChartById(request);
        ChartProto chart = response.getChart();
        return convertChartProtoToChartDto(chart);
    }

    @Override
    public ChartDto updateChart(ChartDto chart) {
        UpdateChartRequest request = UpdateChartRequest.newBuilder().setChart(convertChartDtoToChartProto(chart))
                .build();
        UpdateChartResponse response = chartServiceBlockingStub.updateChart(request);
        ChartProto updatedChart = response.getChart();
        return convertChartProtoToChartDto(updatedChart);
    }

    private ChartDto convertChartProtoToChartDto(ChartProto chart) {
        try {

            ChartDtoBuilder builder = ChartDto.builder();
            builder.id(chart.getId());
            builder.userId(chart.getUserId());
            if (!StringUtils.isNullOrEmpty(chart.getName())) {
                builder.name(chart.getName());
            }
            if (!StringUtils.isNullOrEmpty(chart.getDescription())) {
                builder.description(chart.getDescription());
            }
            if (!StringUtils.isNullOrEmpty(chart.getConfig())) {
                builder.config(
                        MapperUtils.objectMapper.readValue(chart.getConfig(), new TypeReference<Map<String, Object>>() {
                        }));
            }
            if (!StringUtils.isNullOrEmpty(chart.getQueryOption())) {
                builder.queryOption(MapperUtils.objectMapper.readValue(chart.getQueryOption(), JsonNode.class));
            }
            builder.isDeleted(chart.getIsDeleted());
            return builder.build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ChartProto convertChartDtoToChartProto(ChartDto chart) {
        try {
            ChartProto.Builder builder = ChartProto.newBuilder();
            builder.setId(chart.getId());
            builder.setUserId(chart.getUserId());
            if (!StringUtils.isNullOrEmpty(chart.getName())) {
                builder.setName(chart.getName());
            }
            if (!StringUtils.isNullOrEmpty(chart.getDescription())) {
                builder.setDescription(chart.getDescription());
            }
            if (chart.getConfig() != null && !chart.getConfig().isEmpty()) {
                builder.setConfig(MapperUtils.objectMapper.writeValueAsString(chart.getConfig()));
            }
            if (chart.getQueryOption() != null && !chart.getQueryOption().isNull()) {
                builder.setQueryOption(MapperUtils.objectMapper.writeValueAsString(chart.getQueryOption()));
            }
            builder.setIsDeleted(chart.getIsDeleted());
            return builder.build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

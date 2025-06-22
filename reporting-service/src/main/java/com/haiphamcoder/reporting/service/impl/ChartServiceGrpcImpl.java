package com.haiphamcoder.reporting.service.impl;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.Chart.ChartBuilder;
import com.haiphamcoder.reporting.proto.ChartProto;
import com.haiphamcoder.reporting.proto.ChartServiceGrpc;
import com.haiphamcoder.reporting.proto.GetChartByIdRequest;
import com.haiphamcoder.reporting.proto.GetChartByIdResponse;
import com.haiphamcoder.reporting.proto.UpdateChartRequest;
import com.haiphamcoder.reporting.proto.UpdateChartResponse;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.StringUtils;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartServiceGrpcImpl extends ChartServiceGrpc.ChartServiceImplBase {
    private final ChartRepository chartRepository;

    @Override
    public void getChartById(GetChartByIdRequest request, StreamObserver<GetChartByIdResponse> responseObserver) {
        Optional<Chart> chart = chartRepository.getChartById(request.getId());
        if (chart.isPresent()) {
            GetChartByIdResponse response = GetChartByIdResponse.newBuilder()
                    .setChart(this.convertToChartProto(chart.get()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Source not found").asException());
        }
    }

    @Override
    public void updateChart(UpdateChartRequest request, StreamObserver<UpdateChartResponse> responseObserver) {
        Chart chart = this.convertToChart(request.getChart());

        Optional<Chart> savedChart = chartRepository.updateChart(chart);

        if (savedChart.isPresent()) {
            UpdateChartResponse response = UpdateChartResponse.newBuilder()
                    .setChart(this.convertToChartProto(savedChart.get()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to update chart").asException());
        }
    }

    private ChartProto convertToChartProto(Chart chart) {
        ChartProto.Builder builder = ChartProto.newBuilder();
        builder.setId(chart.getId());
        if (!StringUtils.isNullOrEmpty(chart.getName())) {
            builder.setName(chart.getName());
        }
        builder.setUserId(chart.getUserId());
        if (!StringUtils.isNullOrEmpty(chart.getDescription())) {
            builder.setDescription(chart.getDescription());
        }
        if (chart.getConfig() != null && !chart.getConfig().isEmpty()) {
            try {
                builder.setConfig(MapperUtils.objectMapper.writeValueAsString(chart.getConfig()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (chart.getQueryOption() != null && !chart.getQueryOption().isEmpty()) {
            builder.setQueryOption(chart.getQueryOption());
        }
        builder.setIsDeleted(chart.getIsDeleted());
        return builder.build();
    }

    private Chart convertToChart(ChartProto chartProto) {
        ChartBuilder builder = Chart.builder();
        builder.id(chartProto.getId());
        if (!StringUtils.isNullOrEmpty(chartProto.getName())) {
            builder.name(chartProto.getName());
        }
        builder.userId(chartProto.getUserId());
        if (!StringUtils.isNullOrEmpty(chartProto.getDescription())) {
            builder.description(chartProto.getDescription());
        }
        if (chartProto.getConfig() != null && !chartProto.getConfig().isEmpty()) {
            try {
                builder.config(MapperUtils.objectMapper.readTree(chartProto.getConfig()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (chartProto.getQueryOption() != null && !chartProto.getQueryOption().isEmpty()) {
            builder.queryOption(chartProto.getQueryOption());
        }
        builder.isDeleted(chartProto.getIsDeleted());
        return builder.build();
    }
}

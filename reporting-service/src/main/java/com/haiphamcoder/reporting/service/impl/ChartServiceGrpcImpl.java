package com.haiphamcoder.reporting.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.proto.ChartProto;
import com.haiphamcoder.reporting.proto.ChartServiceGrpc;
import com.haiphamcoder.reporting.proto.GetChartByIdRequest;
import com.haiphamcoder.reporting.proto.GetChartByIdResponse;
import com.haiphamcoder.reporting.proto.UpdateChartRequest;
import com.haiphamcoder.reporting.proto.UpdateChartResponse;
import com.haiphamcoder.reporting.repository.ChartRepository;

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
        return ChartProto.newBuilder()
                .setId(chart.getId())
                .build();
    }

    private Chart convertToChart(ChartProto chartProto) {
        return Chart.builder()
                .id(chartProto.getId())
                .build();
    }
}

package com.haiphamcoder.dataprocessing.service.impl;

import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.proto.*;
import com.haiphamcoder.dataprocessing.service.RawDataService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataProcessingServiceGrpcImpl extends DataProcessingServiceGrpc.DataProcessingServiceImplBase {

    private final RawDataService rawDataService;

    @Override
    public void cloneSource(CloneSourceRequest request, StreamObserver<CloneSourceResponse> responseObserver) {
        boolean isSuccess = rawDataService.cloneTable(request.getSourceTable(), request.getTargetTable());
        responseObserver.onNext(CloneSourceResponse.newBuilder().setSuccess(isSuccess).build());
        responseObserver.onCompleted();
    }

}

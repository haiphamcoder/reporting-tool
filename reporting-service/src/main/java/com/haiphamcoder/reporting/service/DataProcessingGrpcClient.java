package com.haiphamcoder.reporting.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.haiphamcoder.dataprocessing.proto.CloneSourceRequest;
import com.haiphamcoder.dataprocessing.proto.CloneSourceResponse;
import com.haiphamcoder.dataprocessing.proto.DataProcessingServiceGrpc;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataProcessingGrpcClient {
    private final DataProcessingServiceGrpc.DataProcessingServiceBlockingStub dataProcessingServiceBlockingStub;

    public DataProcessingGrpcClient(
            @Qualifier("dataProcessingServiceChannel") ManagedChannel dataProcessingServiceChannel) {
        this.dataProcessingServiceBlockingStub = DataProcessingServiceGrpc
                .newBlockingStub(dataProcessingServiceChannel);
    }

    public boolean cloneData(String sourceDatabase, String targetDatabase) {
        CloneSourceRequest request = CloneSourceRequest.newBuilder()
                .setSourceTable(sourceDatabase)
                .setTargetTable(targetDatabase)
                .build();
        CloneSourceResponse response = dataProcessingServiceBlockingStub.cloneSource(request);
        return response.getSuccess();
    }
}

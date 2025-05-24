package com.haiphamcoder.reporting.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.proto.GetSourceByIdRequest;
import com.haiphamcoder.reporting.proto.GetSourceByIdResponse;
import com.haiphamcoder.reporting.proto.SourceProto;
import com.haiphamcoder.reporting.proto.SourceServiceGrpc;
import com.haiphamcoder.reporting.proto.UpdateSourceRequest;
import com.haiphamcoder.reporting.proto.UpdateSourceResponse;
import com.haiphamcoder.reporting.repository.SourceRepository;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceGrpcImpl extends SourceServiceGrpc.SourceServiceImplBase {
    private final SourceRepository sourceRepository;

    @Override
    public void getSourceById(GetSourceByIdRequest request, StreamObserver<GetSourceByIdResponse> responseObserver) {
        Optional<Source> source = sourceRepository.getSourceById(request.getId());
        if (source.isPresent()) {
            GetSourceByIdResponse response = GetSourceByIdResponse.newBuilder()
                    .setSource(this.convertToSourceProto(source.get()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Source not found").asException());
        }
    }

    @Override
    public void updateSource(UpdateSourceRequest request, StreamObserver<UpdateSourceResponse> responseObserver) {
        Source source = this.convertToSource(request.getSource());

        Optional<Source> savedSource = sourceRepository.updateSource(source);

        if (savedSource.isPresent()) {
            UpdateSourceResponse response = UpdateSourceResponse.newBuilder()
                    .setSource(this.convertToSourceProto(savedSource.get()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to update source").asException());
        }
    }

    private SourceProto convertToSourceProto(Source source) {
        return SourceProto.newBuilder()
                .setId(source.getId())
                .setName(source.getName())
                .setDescription(source.getDescription())
                .setConnectorType(source.getConnectorType())
                .setMapping(source.getMapping())
                .setConfig(source.getConfig())
                .setTableName(source.getTableName())
                .setStatus(source.getStatus())
                .setUserId(source.getUserId())
                .setIsDeleted(source.getIsDeleted())
                .setIsStarred(source.getIsStarred())
                .setLastSyncTime(source.getLastSyncTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setCreatedAt(source.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setModifiedAt(source.getModifiedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }

    private Source convertToSource(SourceProto sourceProto) {
        return Source.builder()
                .id(sourceProto.getId())
                .name(sourceProto.getName())
                .description(sourceProto.getDescription())
                .connectorType(sourceProto.getConnectorType())
                .mapping(sourceProto.getMapping())
                .config(sourceProto.getConfig())
                .tableName(sourceProto.getTableName())
                .status(sourceProto.getStatus())
                .userId(sourceProto.getUserId())
                .isDeleted(sourceProto.getIsDeleted())
                .isStarred(sourceProto.getIsStarred())
                .lastSyncTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(sourceProto.getLastSyncTime()), ZoneId.systemDefault()))
                .createdAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(sourceProto.getCreatedAt()), ZoneId.systemDefault()))
                .modifiedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(sourceProto.getModifiedAt()), ZoneId.systemDefault()))
                .build();
    }

}

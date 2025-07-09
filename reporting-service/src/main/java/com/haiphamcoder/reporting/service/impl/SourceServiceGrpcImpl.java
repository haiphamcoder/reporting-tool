package com.haiphamcoder.reporting.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.domain.entity.Source.SourceBuilder;
import com.haiphamcoder.reporting.proto.GetSourceByIdRequest;
import com.haiphamcoder.reporting.proto.GetSourceByIdResponse;
import com.haiphamcoder.reporting.proto.SourceProto;
import com.haiphamcoder.reporting.proto.SourceServiceGrpc;
import com.haiphamcoder.reporting.proto.UpdateSourceRequest;
import com.haiphamcoder.reporting.proto.UpdateSourceResponse;
import com.haiphamcoder.reporting.proto.UpdateStatusSourceRequest;
import com.haiphamcoder.reporting.proto.UpdateStatusSourceResponse;
import com.haiphamcoder.reporting.repository.SourceRepository;
import com.haiphamcoder.reporting.service.PermissionService;
import com.haiphamcoder.reporting.shared.StringUtils;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceGrpcImpl extends SourceServiceGrpc.SourceServiceImplBase {
    private final SourceRepository sourceRepository;
    private final PermissionService permissionService;

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

    @Override
    public void updateStatusSource(UpdateStatusSourceRequest request, StreamObserver<UpdateStatusSourceResponse> responseObserver) {
        long userId = request.getUserId();
        long sourceId = request.getSourceId();
        int status = request.getStatus();
        if (!permissionService.hasEditSourcePermission(userId, sourceId)) {
            responseObserver.onError(Status.PERMISSION_DENIED.withDescription("You are not allowed to update status this source").asException());
        }
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Source not found").asException());
        }
        Source sourceEntity = source.get();
        sourceEntity.setStatus(status);
        Optional<Source> savedSource = sourceRepository.updateSource(sourceEntity);
        if (savedSource.isPresent()) {
            UpdateStatusSourceResponse response = UpdateStatusSourceResponse.newBuilder()
                .setSource(this.convertToSourceProto(savedSource.get()))
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INTERNAL.withDescription("Failed to update source status").asException());
        }
    }

    private SourceProto convertToSourceProto(Source source) {

        SourceProto.Builder builder = SourceProto.newBuilder();
        builder.setId(source.getId());
        if (!StringUtils.isNullOrEmpty(source.getName())) {
            builder.setName(source.getName());
        }
        if (!StringUtils.isNullOrEmpty(source.getDescription())) {
            builder.setDescription(source.getDescription());
        }
        builder.setConnectorType(source.getConnectorType());
        if (source.getMapping() != null && !source.getMapping().isEmpty()) {
            builder.setMapping(source.getMapping());
        }
        if (source.getConfig() != null && !source.getConfig().isEmpty()) {
            builder.setConfig(source.getConfig());
        }
        if (!StringUtils.isNullOrEmpty(source.getTableName())) {
            builder.setTableName(source.getTableName());
        }
        builder.setStatus(source.getStatus());
        builder.setUserId(source.getUserId());
        builder.setIsDeleted(source.getIsDeleted());
        builder.setIsStarred(source.getIsStarred());
        builder.setLastSyncTime(source.getLastSyncTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return builder.build();
    }

    private Source convertToSource(SourceProto sourceProto) {

        SourceBuilder builder = Source.builder();
        builder.id(sourceProto.getId());
        if (!StringUtils.isNullOrEmpty(sourceProto.getName())) {
            builder.name(sourceProto.getName());
        }
        if (!StringUtils.isNullOrEmpty(sourceProto.getDescription())) {
            builder.description(sourceProto.getDescription());
        }
        builder.connectorType(sourceProto.getConnectorType());
        if (!StringUtils.isNullOrEmpty(sourceProto.getMapping())) {
            builder.mapping(sourceProto.getMapping());
        }
        if (!StringUtils.isNullOrEmpty(sourceProto.getConfig())) {
            builder.config(sourceProto.getConfig());
        }
        if (!StringUtils.isNullOrEmpty(sourceProto.getTableName())) {
            builder.tableName(sourceProto.getTableName());
        }
        builder.status(sourceProto.getStatus());
        builder.userId(sourceProto.getUserId());
        builder.isDeleted(sourceProto.getIsDeleted());
        builder.isStarred(sourceProto.getIsStarred());
        builder.lastSyncTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(sourceProto.getLastSyncTime()),
                ZoneId.systemDefault()));
        return builder.build();
    }

}

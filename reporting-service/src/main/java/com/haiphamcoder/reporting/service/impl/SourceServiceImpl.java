package com.haiphamcoder.reporting.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.reporting.service.HdfsFileService;
import com.haiphamcoder.reporting.config.CommonConstants;
import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.domain.dto.UserDto;
import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.domain.entity.SourcePermission;
import com.haiphamcoder.reporting.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceAlreadyExistsException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.domain.model.request.ConfirmSheetRequest;
import com.haiphamcoder.reporting.domain.model.request.InitSourceRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareSourceRequest;
import com.haiphamcoder.reporting.domain.model.request.UpdateSourceRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.mapper.SourceMapper;
import com.haiphamcoder.reporting.repository.SourcePermissionRepository;
import com.haiphamcoder.reporting.repository.SourceRepository;
import com.haiphamcoder.reporting.service.SourceService;
import com.haiphamcoder.reporting.service.UserGrpcClient;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.Pair;
import com.haiphamcoder.reporting.shared.SnowflakeIdGenerator;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {
    private final SourceRepository sourceRepository;
    private final UserGrpcClient userGrpcClient;
    private final SourcePermissionRepository sourcePermissionRepository;
    private final HdfsFileService hdfsFileService;

    @Override
    public SourceDto initSource(Long userId, InitSourceRequest request) {
        if (request.getName() == null) {
            throw new InvalidInputException("name");
        }
        if (sourceRepository.checkSourceName(userId, request.getName())) {
            throw new ResourceAlreadyExistsException("Source name", request.getName());
        }

        if (request.getConnectorType() == null) {
            throw new InvalidInputException("connector_type");
        }

        Source source = Source.builder()
                .id(SnowflakeIdGenerator.getInstance().generateId())
                .name(request.getName())
                .description(request.getDescription())
                .connectorType(request.getConnectorType())
                .tableName("data_" + userId + "_" + System.currentTimeMillis())
                .userId(userId)
                .status(CommonConstants.SOURCE_STATUS_INIT)
                .build();

        Optional<Source> createdSource = sourceRepository.createSource(source);
        if (createdSource.isPresent()) {
            SourcePermission sourcePermission = SourcePermission.builder()
                    .sourceId(createdSource.get().getId())
                    .userId(userId)
                    .permission(CommonConstants.SOURCE_PERMISSION_ALL)
                    .build();
            sourcePermissionRepository.createSourcePermission(sourcePermission);

            return SourceMapper.toDto(createdSource.get());
        }
        throw new RuntimeException("Create source failed");
    }

    @Override
    public Boolean checkSourceName(Long userId, String sourceName) {
        return sourceRepository.checkSourceName(userId, sourceName);
    }

    @Override
    public SourceDto getSourceById(Long sourceId) {
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            return SourceMapper.toDto(source.get());
        }
        throw new ResourceNotFoundException("Source", sourceId);
    }

    @Override
    public Pair<List<SourceDto>, Metadata> getAllSourcesByUserId(Long userId, String search, Integer page,
            Integer limit) {
        Page<Source> sources = sourceRepository.getAllSourcesByUserId(userId, search, page, limit);

        return new Pair<>(sources.stream()
                .map(source -> {
                    SourceDto sourceDto = SourceMapper.toDto(source);
                    UserDto userDto = userGrpcClient.getUserById(source.getUserId());
                    sourceDto.setOwner(SourceDto.Owner.builder()
                            .id(String.valueOf(userDto.getId()))
                            .name(userDto.getFirstName() + " " + userDto.getLastName())
                            .email(userDto.getEmail())
                            .avatar(userDto.getAvatarUrl())
                            .build());
                    return sourceDto;
                })
                .toList(),
                Metadata.builder()
                        .totalElements(sources.getTotalElements())
                        .numberOfElements(sources.getNumberOfElements())
                        .totalPages(sources.getTotalPages())
                        .currentPage(sources.getNumber())
                        .pageSize(sources.getSize())
                        .build());
    }

    @Override
    public SourceDto createSource(SourceDto sourceDto) {
        Optional<Source> createdSource = sourceRepository.createSource(SourceMapper.toEntity(sourceDto));
        if (createdSource.isPresent()) {
            return SourceMapper.toDto(createdSource.get());
        }
        throw new RuntimeException("Create source failed");
    }

    @Override
    public String uploadFile(Long userId, Long sourceId, MultipartFile file) {

        if (file.isEmpty()) {
            throw new InvalidInputException("file");
        }

        String fileName = file.getOriginalFilename();
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new InvalidInputException("file_name");
        }

        if (hasWritePermission(userId, sourceId)) {
            Optional<Source> source = sourceRepository.getSourceById(sourceId);
            if (source.isPresent()) {
                String config = source.get().getConfig();
                ObjectNode objectNode = null;
                if (StringUtils.isNullOrEmpty(config)) {
                    objectNode = MapperUtils.objectMapper.createObjectNode();
                } else {
                    try {
                        objectNode = MapperUtils.objectMapper.readValue(config, ObjectNode.class);
                    } catch (IOException e) {
                        throw new InvalidInputException("config");
                    }
                }

                Integer connectorType = source.get().getConnectorType();
                if (connectorType == CommonConstants.CONNECTOR_TYPE_CSV
                        || connectorType == CommonConstants.CONNECTOR_TYPE_EXCEL) {
                    try {
                        String filePath = hdfsFileService.uploadFile(userId, file.getInputStream(),
                                connectorType + "/" + fileName.trim().replaceAll("\s+", "_"));
                        objectNode.put("file_path", filePath);
                        source.get().setConfig(objectNode.toString());
                        sourceRepository.updateSource(source.get());

                        return filePath;
                    } catch (IOException e) {
                        throw new RuntimeException("Upload file failed");
                    }
                } else {
                    throw new RuntimeException("Unsupported connector type");
                }
            } else {
                throw new ResourceNotFoundException("Source", sourceId);
            }
        } else {
            throw new ForbiddenException("You are not allowed to upload file to this source");
        }
    }

    @Override
    public Map<String, String> getHistoryUploadFile(Long userId, Integer connectorType) {
        return hdfsFileService.getHistoryUploadFile(userId, connectorType);
    }

    @Override
    public SourceDto confirmSchema(Long userId, SourceDto sourceDto) {

        if (sourceDto.getId() == null) {
            throw new InvalidInputException("source_id");
        }

        if (sourceDto.getMapping() == null) {
            throw new InvalidInputException("mapping");
        }

        if (hasWritePermission(userId, Long.parseLong(sourceDto.getId()))) {
            Optional<Source> existingSource = sourceRepository.getSourceById(Long.parseLong(sourceDto.getId()));
            if (existingSource.isPresent()) {
                try {
                    existingSource.get()
                            .setMapping(MapperUtils.objectMapper.writeValueAsString(sourceDto.getMapping()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Invalid mapping format");
                }
                Optional<Source> updatedSource = sourceRepository.updateSource(existingSource.get());
                if (updatedSource.isPresent()) {
                    return SourceMapper.toDto(updatedSource.get());
                } else {
                    throw new RuntimeException("Update source failed");
                }
            } else {
                throw new ResourceNotFoundException("Source", sourceDto.getId());
            }
        } else {
            throw new ForbiddenException("You are not allowed to update this source");
        }
    }

    @Override
    public void confirmSheet(Long userId, Long sourceId, ConfirmSheetRequest confirmSheetRequest) {
        if (hasWritePermission(userId, sourceId)) {
            Optional<Source> source = sourceRepository.getSourceById(sourceId);
            if (source.isPresent()) {
                if (source.get().getConnectorType() == CommonConstants.CONNECTOR_TYPE_EXCEL) {
                    if (StringUtils.isNullOrEmpty(confirmSheetRequest.getSheetName())) {
                        throw new InvalidInputException("sheet_name");
                    }
                    if (StringUtils.isNullOrEmpty(confirmSheetRequest.getDataRangeSelected())) {
                        throw new InvalidInputException("data_range_selected");
                    }
                    String config = source.get().getConfig();
                    ObjectNode objectNode = null;
                    if (StringUtils.isNullOrEmpty(config)) {
                        objectNode = MapperUtils.objectMapper.createObjectNode();
                    } else {
                        try {
                            objectNode = MapperUtils.objectMapper.readValue(config, ObjectNode.class);
                        } catch (IOException e) {
                            throw new InvalidInputException("config");
                        }
                    }
                    objectNode.put("sheet_name", confirmSheetRequest.getSheetName());
                    objectNode.put("data_range_selected", confirmSheetRequest.getDataRangeSelected());
                    source.get().setConfig(objectNode.toString());
                    Optional<Source> updatedSource = sourceRepository.updateSource(source.get());
                    if (updatedSource.isPresent()) {
                        return;
                    } else {
                        throw new RuntimeException("Update source failed");
                    }
                } else {
                    throw new RuntimeException("Unsupported connector type");
                }
            } else {
                throw new ResourceNotFoundException("Source", sourceId);
            }
        }
    }

    private boolean hasWritePermission(Long userId, Long sourceId) {
        Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                .getSourcePermissionBySourceIdAndUserId(sourceId, userId);
        if (sourcePermission.isPresent()) {
            return sourcePermission.get().getPermission().charAt(1) == 'w';
        }
        return false;
    }

    @Override
    public void deleteSource(Long sourceId) {
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            source.get().setIsDeleted(true);
            sourceRepository.updateSource(source.get());
        } else {
            throw new ResourceNotFoundException("Source", sourceId);
        }
    }

    @Override
    public SourceDto updateSource(Long userId, Long sourceId, UpdateSourceRequest request) {
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            source.get().setName(request.getName());
            source.get().setDescription(request.getDescription());
            Optional<Source> updatedSource = sourceRepository.updateSource(source.get());
            if (updatedSource.isPresent()) {
                return SourceMapper.toDto(updatedSource.get());
            } else {
                throw new RuntimeException("Update source failed");
            }
        } else {
            throw new ResourceNotFoundException("Source", sourceId);
        }
    }

    @Override
    public void shareSource(Long userId, Long sourceId, ShareSourceRequest shareSourceRequest) {
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            Source sourceEntity = source.get();
            if (sourceEntity.getUserId().longValue() != userId.longValue()) {
                throw new ForbiddenException("You are not allowed to share this source");
            }
            for (ShareSourceRequest.UserSourcePermission userSourcePermission : shareSourceRequest
                    .getUserSourcePermissions()) {
                Optional<SourcePermission> existingSourcePermission = sourcePermissionRepository
                        .getSourcePermissionBySourceIdAndUserId(sourceEntity.getId(), userSourcePermission.getUserId());
                if (existingSourcePermission.isPresent()) {
                    existingSourcePermission.get().setPermission(userSourcePermission.getPermission().getValue());
                    sourcePermissionRepository.saveSourcePermission(existingSourcePermission.get());
                } else {
                    SourcePermission sourcePermission = SourcePermission.builder()
                            .sourceId(sourceEntity.getId())
                            .userId(userSourcePermission.getUserId())
                            .permission(userSourcePermission.getPermission().getValue())
                            .build();
                    sourcePermissionRepository.createSourcePermission(sourcePermission);
                }
            }
        } else {
            throw new ResourceNotFoundException("Source", sourceId);
        }
    }

}

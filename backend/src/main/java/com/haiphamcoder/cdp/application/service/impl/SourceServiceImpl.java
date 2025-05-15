package com.haiphamcoder.cdp.application.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.adapter.dto.SourceDto.Mapping;
import com.haiphamcoder.cdp.adapter.dto.mapper.SourceMapper;
import com.haiphamcoder.cdp.application.service.CSVProcessingService;
import com.haiphamcoder.cdp.application.service.HdfsFileService;
import com.haiphamcoder.cdp.application.service.SourceService;
import com.haiphamcoder.cdp.application.service.UserService;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.entity.SourcePermission;
import com.haiphamcoder.cdp.domain.entity.User;
import com.haiphamcoder.cdp.domain.exception.DuplicateSourceNameException;
import com.haiphamcoder.cdp.domain.exception.MissingRequiredFieldException;
import com.haiphamcoder.cdp.domain.exception.PermissionDeniedException;
import com.haiphamcoder.cdp.domain.exception.SourceNotFoundException;
import com.haiphamcoder.cdp.domain.exception.UserNotFoundException;
import com.haiphamcoder.cdp.domain.model.PreviewData;
import com.haiphamcoder.cdp.domain.model.PreviewDataRequest;
import com.haiphamcoder.cdp.domain.repository.SourcePermissionRepository;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.shared.MapperUtils;
import com.haiphamcoder.cdp.shared.SnowflakeIdGenerator;
import com.haiphamcoder.cdp.shared.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {
    private final SourceRepository sourceRepository;
    private final SourcePermissionRepository sourcePermissionRepository;
    private final HdfsFileService hdfsFileService;
    private final CSVProcessingService csvProcessingService;
    private final UserService userService;

    @Override
    public SourceDto initSource(String userId, SourceDto sourceDto) {
        if (sourceDto.getName() == null) {
            throw new MissingRequiredFieldException("name");
        }
        if (sourceRepository.checkSourceName(userId, sourceDto.getName())) {
            throw new DuplicateSourceNameException("Source name already exists");
        }

        if (sourceDto.getConnectorType() == null) {
            throw new MissingRequiredFieldException("connector_type");
        }

        User user = userService.getUserById(Long.parseLong(userId));
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Source source = Source.builder()
                .id(SnowflakeIdGenerator.getInstance().generateId())
                .name(sourceDto.getName())
                .connectorType(sourceDto.getConnectorType())
                .tableName("data_" + userId + "_" + System.currentTimeMillis())
                .user(user)
                .status(CommonConstants.SOURCE_STATUS_INIT)
                .build();

        Optional<Source> createdSource = sourceRepository.createSource(source);
        if (createdSource.isPresent()) {

            SourcePermission sourcePermission = SourcePermission.builder()
                    .source(createdSource.get())
                    .user(user)
                    .permission(CommonConstants.SOURCE_PERMISSION_ALL)
                    .build();
            sourcePermissionRepository.createSourcePermission(sourcePermission);

            return SourceMapper.toDto(createdSource.get());
        }
        throw new RuntimeException("Create source failed");
    }

    @Override
    public Boolean checkSourceName(String userId, String sourceName) {
        return sourceRepository.checkSourceName(userId, sourceName);
    }

    @Override
    public SourceDto getSourceById(Long sourceId) {
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            return SourceMapper.toDto(source.get());
        }
        throw new RuntimeException("Source not found");
    }

    @Override
    public List<SourceDto> getAllSourcesByUserId(Long userId) {
        return sourceRepository.getAllSourcesByUserId(userId).stream()
                .map(SourceMapper::toDto)
                .collect(Collectors.toList());
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
    public String uploadFile(String userId, Long sourceId, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new RuntimeException("File name is required");
        }

        if (hasWritePermission(Long.valueOf(userId), sourceId)) {
            Optional<Source> source = sourceRepository.getSourceById(sourceId);
            if (source.isPresent()) {
                Map<String, Object> config = source.get().getConfig();
                if (config == null) {
                    config = new HashMap<>();
                }
                Integer connectorType = source.get().getConnectorType();

                if (connectorType == CommonConstants.CONNECTOR_TYPE_CSV) {
                    try {
                        String filePath = hdfsFileService.uploadFile(userId, file.getInputStream(),
                                connectorType + "/" + fileName.trim().replaceAll("\\s+", "_"));
                        config.put("file_path", filePath);
                        source.get().setConfig(config);

                        List<Mapping> schema = csvProcessingService.getSchema(source.get());
                        source.get().setMapping(MapperUtils.objectMapper.convertValue(schema, JsonNode.class));
                        sourceRepository.updateSource(source.get());

                        return filePath;
                    } catch (IOException e) {
                        throw new RuntimeException("Upload file failed");
                    }
                } else {
                    throw new RuntimeException("Unsupported connector type");
                }
            } else {
                throw new SourceNotFoundException("Source not found");
            }
        } else {
            throw new PermissionDeniedException("You are not allowed to upload file to this source");
        }
    }

    @Override
    public Map<String, String> getHistoryUploadFile(String userId, Integer connectorType) {
        return hdfsFileService.getHistoryUploadFile(userId, connectorType);
    }

    @Override
    public PreviewData getPreviewData(String userId, PreviewDataRequest previewDataRequest) {
        if (!previewDataRequest.getPath().contains(userId + "/" + previewDataRequest.getConnectorType())) {
            throw new PermissionDeniedException("You are not allowed to access this file");
        }

        switch (previewDataRequest.getConnectorType()) {
            case CommonConstants.CONNECTOR_TYPE_CSV:
                return csvProcessingService.getPreviewData(userId, previewDataRequest.getPath(),
                        previewDataRequest.getLimit());
            default:
                throw new RuntimeException("Unsupported connector type");
        }
    }

    @Override
    public SourceDto updateSchema(String userId, SourceDto sourceDto) {

        if (sourceDto.getId() == null) {
            throw new MissingRequiredFieldException("source_id");
        }

        if (sourceDto.getMapping() == null) {
            throw new MissingRequiredFieldException("mapping");
        }

        if (hasWritePermission(Long.valueOf(userId), sourceDto.getId())) {
            Optional<Source> existingSource = sourceRepository.getSourceById(sourceDto.getId());
            if (existingSource.isPresent()) {
                existingSource.get()
                        .setMapping(MapperUtils.objectMapper.convertValue(sourceDto.getMapping(), JsonNode.class));
                Optional<Source> updatedSource = sourceRepository.updateSource(existingSource.get());
                if (updatedSource.isPresent()) {
                    return SourceMapper.toDto(updatedSource.get());
                }
                throw new RuntimeException("Update source failed");
            } else {
                throw new SourceNotFoundException("Source not found");
            }
        } else {
            throw new PermissionDeniedException("You are not allowed to update this source");
        }
    }

    @Override
    public List<Mapping> getSchema(String userId, SourceDto sourceDto) {
        if (sourceDto.getId() == null) {
            throw new MissingRequiredFieldException("source_id");
        }

        if (hasReadPermission(Long.valueOf(userId), sourceDto.getId())) {
            Optional<Source> source = sourceRepository.getSourceById(sourceDto.getId());
            if (source.isPresent()) {
                if (source.get().getMapping() != null) {
                    try {
                        return MapperUtils.objectMapper.readValue(source.get().getMapping().toString(),
                                new TypeReference<List<Mapping>>() {
                                });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Get schema failed");
                    }
                } else {
                    if (source.get().getConnectorType() == CommonConstants.CONNECTOR_TYPE_CSV) {
                        return csvProcessingService.getSchema(source.get());
                    } else {
                        throw new RuntimeException("Unsupported connector type");
                    }
                }
            }
            throw new SourceNotFoundException("Source not found");
        } else {
            throw new PermissionDeniedException("You are not allowed to get schema of this source");
        }
    }

    private Boolean hasReadPermission(Long userId, Long sourceId) {
        Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                .getSourcePermissionBySourceIdAndUserId(sourceId, userId);
        if (sourcePermission.isPresent()) {
            return sourcePermission.get().getPermission().charAt(0) == 'r';
        }
        return false;
    }

    private Boolean hasWritePermission(Long userId, Long sourceId) {
        Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                .getSourcePermissionBySourceIdAndUserId(sourceId, userId);
        if (sourcePermission.isPresent()) {
            return sourcePermission.get().getPermission().charAt(1) == 'w';
        }
        return false;
    }

}

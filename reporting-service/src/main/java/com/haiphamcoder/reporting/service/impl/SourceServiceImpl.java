package com.haiphamcoder.reporting.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haiphamcoder.reporting.service.HdfsFileService;
import com.haiphamcoder.reporting.config.CommonConstants;
import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.domain.entity.SourcePermission;
import com.haiphamcoder.reporting.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceAlreadyExistsException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.mapper.SourceMapper;
import com.haiphamcoder.reporting.repository.SourcePermissionRepository;
import com.haiphamcoder.reporting.repository.SourceRepository;
import com.haiphamcoder.reporting.service.SourceService;
import com.haiphamcoder.reporting.shared.MapperUtils;
import com.haiphamcoder.reporting.shared.SnowflakeIdGenerator;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {
    private final SourceRepository sourceRepository;
    private final SourcePermissionRepository sourcePermissionRepository;
    private final HdfsFileService hdfsFileService;

    @Override
    public SourceDto initSource(Long userId, SourceDto sourceDto) {
        if (sourceDto.getName() == null) {
            throw new InvalidInputException("name");
        }
        if (sourceRepository.checkSourceName(userId, sourceDto.getName())) {
            throw new ResourceAlreadyExistsException("Source name", sourceDto.getName());
        }

        if (sourceDto.getConnectorType() == null) {
            throw new InvalidInputException("connector_type");
        }

        Source source = Source.builder()
                .id(SnowflakeIdGenerator.getInstance().generateId())
                .name(sourceDto.getName())
                .connectorType(sourceDto.getConnectorType())
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
                if (connectorType == CommonConstants.CONNECTOR_TYPE_CSV) {
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

        if (hasWritePermission(Long.valueOf(userId), sourceDto.getId())) {
            Optional<Source> existingSource = sourceRepository.getSourceById(sourceDto.getId());
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

    private Boolean hasWritePermission(Long userId, Long sourceId) {
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

}

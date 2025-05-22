package com.haiphamcoder.reporting.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.reporting.service.HdfsFileService;
import com.haiphamcoder.reporting.config.CommonConstants;
import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.domain.entity.Source;
import com.haiphamcoder.reporting.domain.entity.SourcePermission;
import com.haiphamcoder.reporting.domain.exception.DuplicateSourceNameException;
import com.haiphamcoder.reporting.domain.exception.MissingRequiredFieldException;
import com.haiphamcoder.reporting.domain.exception.PermissionDeniedException;
import com.haiphamcoder.reporting.domain.exception.SourceNotFoundException;
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
            throw new MissingRequiredFieldException("name");
        }
        if (sourceRepository.checkSourceName(userId, sourceDto.getName())) {
            throw new DuplicateSourceNameException("Source name already exists");
        }

        if (sourceDto.getConnectorType() == null) {
            throw new MissingRequiredFieldException("connector_type");
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
    public String uploadFile(Long userId, Long sourceId, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new RuntimeException("File name is required");
        }

        if (hasWritePermission(userId, sourceId)) {
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
    public Map<String, String> getHistoryUploadFile(Long userId, Integer connectorType) {
        return hdfsFileService.getHistoryUploadFile(userId, connectorType);
    }

    @Override
    public SourceDto confirmSchema(Long userId, SourceDto sourceDto) {

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
                    // TODO: Submit source to raw data service
                }
                throw new RuntimeException("Update source failed");
            } else {
                throw new SourceNotFoundException("Source not found");
            }
        } else {
            throw new PermissionDeniedException("You are not allowed to update this source");
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

}

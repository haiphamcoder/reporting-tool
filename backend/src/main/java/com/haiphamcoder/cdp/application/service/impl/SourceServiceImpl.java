package com.haiphamcoder.cdp.application.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.adapter.dto.mapper.SourceMapper;
import com.haiphamcoder.cdp.application.service.CSVProcessingService;
import com.haiphamcoder.cdp.application.service.HdfsFileService;
import com.haiphamcoder.cdp.application.service.SourceService;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.exception.PermissionDeniedException;
import com.haiphamcoder.cdp.domain.model.PreviewData;
import com.haiphamcoder.cdp.domain.model.PreviewDataRequest;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;
import com.haiphamcoder.cdp.infrastructure.config.CommonConstants;
import com.haiphamcoder.cdp.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {
    private final SourceRepository sourceRepository;
    private final HdfsFileService hdfsFileService;
    private final CSVProcessingService csvProcessingService;

    @Override
    public Source getSourceById(Long sourceId) {
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            return source.get();
        }
        throw new RuntimeException("Source not found");
    }

    @Override
    public List<Source> getAllSourcesByUserId(Long userId) {
        return sourceRepository.getAllSourcesByUserId(userId);
    }

    @Override
    public Source createSource(SourceDto sourceDto) {
        Optional<Source> createdSource = sourceRepository.createSource(SourceMapper.toEntity(sourceDto));
        if (createdSource.isPresent()) {
            return createdSource.get();
        }
        throw new RuntimeException("Create source failed");
    }

    @Override
    public String uploadFile(String userId, Integer connectorType, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (StringUtils.isNullOrEmpty(fileName)) {
            throw new RuntimeException("File name is required");
        }
        String filePath;
        try {
            filePath = hdfsFileService.uploadFile(userId, file.getInputStream(),
                    connectorType + "/" + fileName.trim().replaceAll("\\s+", "_"));
        } catch (IOException e) {
            throw new RuntimeException("Upload file failed");
        }
        return filePath;
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
    public void confirmSchema(String userId, Long sourceId, Map<String, String> mapping) {
        Optional<Source> source = sourceRepository.getSourceById(sourceId);
        if (source.isPresent()) {
            Map<String, Object> schema = source.get().getMapping();
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                schema.put(entry.getKey(), entry.getValue());
            }
            source.get().setMapping(schema);
            sourceRepository.createSource(source.get());
        } else {
            throw new RuntimeException("Source not found");
        }
    }

}

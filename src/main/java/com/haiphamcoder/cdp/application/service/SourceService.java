package com.haiphamcoder.cdp.application.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.model.PreviewData;
import com.haiphamcoder.cdp.domain.model.PreviewDataRequest;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;
import com.haiphamcoder.cdp.shared.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;
    private final HdfsFileService hdfsFileService;

    public List<Source> getAllSourcesByUserId(Long userId) {
        return sourceRepository.getAllSourcesByUserId(userId);
    }

    public Source deleteSourceById(Long id) {
        Optional<Source> deletedSource = sourceRepository.deleteSourceById(id);
        if (deletedSource.isPresent()) {
            return deletedSource.get();
        }
        throw new RuntimeException("Source not found");
    }

    public Source createSource(Source source) {
        Optional<Source> createdSource = sourceRepository.createSource(source);
        if (createdSource.isPresent()) {
            return createdSource.get();
        }
        throw new RuntimeException("Create source failed");
    }

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

    public Map<String, String> getHistoryUploadFile(String userId, Integer connectorType) {
        return hdfsFileService.getHistoryUploadFile(userId, connectorType);
    }

    public PreviewData getPreviewData(String userId, PreviewDataRequest previewDataRequest) {
        return null;
    }

}
package com.haiphamcoder.reporting.service;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.reporting.domain.dto.SourceDto;

public interface SourceService {

    public SourceDto initSource(Long userId,SourceDto sourceDto);

    public Boolean checkSourceName(Long userId, String sourceName);

    public SourceDto getSourceById(Long sourceId);

    public List<SourceDto> getAllSourcesByUserId(Long userId);

    public SourceDto createSource(SourceDto sourceDto);

    public String uploadFile(Long userId, Long sourceId, MultipartFile file);

    public Map<String, String> getHistoryUploadFile(Long userId, Integer connectorType);

    public SourceDto confirmSchema(Long userId, SourceDto sourceDto);

}
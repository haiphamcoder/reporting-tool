package com.haiphamcoder.cdp.application.service;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.adapter.dto.SourceDto.Mapping;
import com.haiphamcoder.cdp.domain.model.PreviewData;
import com.haiphamcoder.cdp.domain.model.PreviewDataRequest;

public interface SourceService {

    public SourceDto initSource(String userId,SourceDto sourceDto);

    public Boolean checkSourceName(String userId, String sourceName);

    public SourceDto getSourceById(Long sourceId);

    public List<SourceDto> getAllSourcesByUserId(Long userId);

    public SourceDto createSource(SourceDto sourceDto);

    public String uploadFile(String userId, Long sourceId, MultipartFile file);

    public Map<String, String> getHistoryUploadFile(String userId, Integer connectorType);

    public PreviewData getPreviewData(String userId, PreviewDataRequest previewDataRequest);

    public SourceDto updateSchema(String userId, SourceDto sourceDto);

    public List<Mapping> getSchema(String userId, SourceDto sourceDto);

}
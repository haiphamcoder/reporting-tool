package com.haiphamcoder.cdp.application.service;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.cdp.adapter.dto.SourceDto;
import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.model.PreviewData;
import com.haiphamcoder.cdp.domain.model.PreviewDataRequest;

public interface SourceService {

    public Source getSourceById(Long sourceId);

    public List<Source> getAllSourcesByUserId(Long userId);

    public Source createSource(SourceDto sourceDto);

    public String uploadFile(String userId, Integer connectorType, MultipartFile file);

    public Map<String, String> getHistoryUploadFile(String userId, Integer connectorType);

    public PreviewData getPreviewData(String userId, PreviewDataRequest previewDataRequest);

    public void confirmSchema(String userId, Long sourceId, Map<String, String> mapping);

}
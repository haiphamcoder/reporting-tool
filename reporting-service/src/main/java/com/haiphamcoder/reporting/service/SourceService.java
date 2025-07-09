package com.haiphamcoder.reporting.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.haiphamcoder.reporting.domain.dto.SourceDto;
import com.haiphamcoder.reporting.domain.dto.SourceDto.UserSourcePermission;
import com.haiphamcoder.reporting.domain.model.request.ConfirmSheetRequest;
import com.haiphamcoder.reporting.domain.model.request.InitSourceRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareSourceRequest;
import com.haiphamcoder.reporting.domain.model.request.UpdateSourceRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.shared.Pair;

public interface SourceService {

    public SourceDto initSource(Long userId, InitSourceRequest sourceDto);

    public Boolean checkSourceName(Long userId, String sourceName);

    public SourceDto getSourceById(Long userId, Long sourceId);

    public Pair<List<SourceDto>, Metadata> getAllSourcesByUserId(Long userId, String search, Integer page, Integer limit);

    public String uploadFile(Long userId, Long sourceId, MultipartFile file);

    public Map<String, String> getHistoryUploadFile(Long userId, Integer connectorType);

    public SourceDto confirmSchema(Long userId, SourceDto sourceDto);

    public SourceDto updateStatusSource(Long userId, Long sourceId, Integer status);

    public void confirmSheet(Long userId, Long sourceId, ConfirmSheetRequest confirmSheetRequest);

    public void deleteSource(Long userId, Long sourceId);

    public SourceDto updateSource(Long userId, Long sourceId, UpdateSourceRequest request);

    public void shareSource(Long userId, Long sourceId, ShareSourceRequest shareSourceRequest);

    public List<UserSourcePermission> getShareSource(Long userId, Long sourceId);

    public void cloneSource(Long userId, Long sourceId);

}
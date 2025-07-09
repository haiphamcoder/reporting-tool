package com.haiphamcoder.dataprocessing.service;

import com.haiphamcoder.dataprocessing.domain.dto.SourceDto;

public interface SourceGrpcClient {

    public SourceDto getSourceById(Long id);

    public SourceDto updateSource(SourceDto source);

    public void updateSourceStatus(Long userId, Long sourceId, Integer status);

}

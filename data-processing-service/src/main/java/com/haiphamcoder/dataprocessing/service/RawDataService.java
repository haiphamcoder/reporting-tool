package com.haiphamcoder.dataprocessing.service;

import com.haiphamcoder.dataprocessing.domain.model.PreviewData;

public interface RawDataService {

    PreviewData previewSource(Long sourceId, Integer page, Integer limit);

}

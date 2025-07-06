package com.haiphamcoder.dataprocessing.service;

import com.haiphamcoder.dataprocessing.domain.model.GetChartPreviewDataRequest;
import com.haiphamcoder.dataprocessing.domain.model.PreviewData;
import com.haiphamcoder.dataprocessing.domain.model.request.UpdateSourceDataRequest;

public interface RawDataService {

    PreviewData previewSource(Long sourceId, Integer page, Integer limit);

    PreviewData getChartPreviewData(GetChartPreviewDataRequest request, Integer page, Integer limit);

    void updateSourceData(Long sourceId, UpdateSourceDataRequest request);

}

package com.haiphamcoder.reporting.service;

import java.util.List;

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.dto.ChartDto.UserChartPermission;
import com.haiphamcoder.reporting.domain.model.QueryOption;
import com.haiphamcoder.reporting.domain.model.request.CreateChartRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareChartRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.shared.Pair;

public interface ChartService {

    Pair<List<ChartDto>, Metadata> getAllChartsByUserId(Long userId, String search, Integer page, Integer limit);

    ChartDto getChartById(Long userId,Long chartId);

    ChartDto updateChart(Long userId, Long chartId, ChartDto chartDto);

    void deleteChart(Long userId, Long chartId);

    ChartDto createChart(Long userId, CreateChartRequest request);

    String convertQueryToSql(Long userId, QueryOption queryOption);

    void updateShareChart(Long userId, Long chartId, ShareChartRequest shareChartRequest);

    List<UserChartPermission> getShareChart(Long userId, Long chartId);

    ChartDto cloneChart(Long userId, Long chartId);

}

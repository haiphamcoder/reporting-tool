package com.haiphamcoder.reporting.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.domain.dto.ChartDto;
import com.haiphamcoder.reporting.domain.dto.ChartDto.UserChartPermission;
import com.haiphamcoder.reporting.domain.dto.UserDto;
import com.haiphamcoder.reporting.domain.entity.Chart;
import com.haiphamcoder.reporting.domain.entity.ChartPermission;
import com.haiphamcoder.reporting.domain.entity.SourcePermission;
import com.haiphamcoder.reporting.domain.enums.ChartPermissionType;
import com.haiphamcoder.reporting.domain.exception.business.detail.ForbiddenException;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;
import com.haiphamcoder.reporting.domain.exception.business.detail.ResourceNotFoundException;
import com.haiphamcoder.reporting.domain.model.QueryOption;
import com.haiphamcoder.reporting.domain.model.QueryOption.Join;
import com.haiphamcoder.reporting.domain.model.request.CreateChartRequest;
import com.haiphamcoder.reporting.domain.model.request.ShareChartRequest;
import com.haiphamcoder.reporting.domain.model.response.Metadata;
import com.haiphamcoder.reporting.mapper.ChartMapper;
import com.haiphamcoder.reporting.repository.ChartPermissionRepository;
import com.haiphamcoder.reporting.repository.ChartRepository;
import com.haiphamcoder.reporting.repository.SourcePermissionRepository;
import com.haiphamcoder.reporting.service.ChartService;
import com.haiphamcoder.reporting.service.PermissionService;
import com.haiphamcoder.reporting.service.UserGrpcClient;
import com.haiphamcoder.reporting.shared.Pair;
import com.haiphamcoder.reporting.shared.SnowflakeIdGenerator;
import com.haiphamcoder.reporting.shared.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {
    private final ChartRepository chartRepository;
    private final ChartPermissionRepository chartPermissionRepository;
    private final SourcePermissionRepository sourcePermissionRepository;
    private final UserGrpcClient userGrpcClient;
    private final PermissionService permissionService;

    @Override
    public Pair<List<ChartDto>, Metadata> getAllChartsByUserId(Long userId, String search, Integer page,
            Integer limit) {
        List<ChartPermission> chartPermissions = chartPermissionRepository.getAllChartPermissionsByUserId(userId);
        Set<Long> chartIds = chartPermissions.stream().map(ChartPermission::getChartId).collect(Collectors.toSet());
        Page<Chart> charts = chartRepository.getAllChartsByUserIdOrChartId(userId, chartIds, search, page, limit);
        return new Pair<>(charts.stream().map(chart -> {
            ChartDto chartDto = ChartMapper.toChartDto(chart);
            UserDto userDto = userGrpcClient.getUserById(chart.getUserId());
            chartDto.setOwner(ChartDto.Owner.builder()
                    .id(String.valueOf(userDto.getId()))
                    .name(userDto.getFirstName() + " " + userDto.getLastName())
                    .email(userDto.getEmail())
                    .avatar(userDto.getAvatarUrl())
                    .build());
            chartDto.setCanEdit(permissionService.hasEditChartPermission(userId, chart.getId()));
            chartDto.setCanShare(permissionService.hasOwnerChartPermission(userId, chart.getId()));
            return chartDto;
        }).toList(),
                Metadata.builder()
                        .totalElements(charts.getTotalElements())
                        .numberOfElements(charts.getNumberOfElements())
                        .totalPages(charts.getTotalPages())
                        .currentPage(charts.getNumber())
                        .pageSize(charts.getSize())
                        .build());
    }

    @Override
    public ChartDto getChartById(Long userId, Long chartId) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        if (!Objects.equals(chart.get().getUserId(), userId)
                && !permissionService.hasViewChartPermission(userId, chartId)) {
            throw new ForbiddenException("You are not allowed to view this chart");
        }
        return ChartMapper.toChartDto(chart.get());
    }

    @Override
    public ChartDto updateChart(Long userId, Long chartId, ChartDto chartDto) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        if (!Objects.equals(chart.get().getUserId(), userId)
                && !permissionService.hasEditChartPermission(userId, chartId)) {
            throw new ForbiddenException("You are not allowed to edit this chart");
        }
        ChartDto updatedChartDto = ChartMapper.updateChartDto(chart.get(), chartDto);
        Chart updatedChart = chartRepository.save(ChartMapper.toChart(updatedChartDto));
        if (updatedChart == null) {
            throw new RuntimeException("Update chart failed");
        }
        return ChartMapper.toChartDto(updatedChart);
    }

    @Override
    public void deleteChart(Long userId, Long chartId) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        if (!Objects.equals(chart.get().getUserId(), userId)) {
            if (permissionService.hasViewChartPermission(userId, chartId)) {
                chartPermissionRepository.deleteAllChartPermissionsByChartIdAndUserId(chartId, userId);
            } else {
                throw new ForbiddenException("You are not allowed to delete this chart");
            }
        } else {
            chart.get().setIsDeleted(true);
            chartRepository.updateChart(chart.get());
            chartPermissionRepository.deleteAllChartPermissionsByChartIdAndUserIdNot(chartId, userId);
        }
    }

    @Override
    public ChartDto createChart(Long userId, CreateChartRequest request) {
        if (StringUtils.isNullOrEmpty(request.getName())) {
            throw new InvalidInputException("name");
        }

        ChartDto chartDto = ChartDto.builder()
                .config(request.getConfig())
                .name(request.getName())
                .description(request.getDescription())
                .sqlQuery(request.getSqlQuery())
                .build();
        Chart chart = ChartMapper.toChart(chartDto);
        chart.setId(SnowflakeIdGenerator.getInstance().generateId());
        chart.setUserId(userId);

        Chart savedChart = chartRepository.save(chart);
        if (savedChart == null) {
            throw new RuntimeException("Create chart failed");
        }

        ChartPermission chartPermission = ChartPermission.builder()
                .chartId(savedChart.getId())
                .userId(userId)
                .permission(ChartPermissionType.OWNER.getValue())
                .build();
        chartPermissionRepository.saveChartPermission(chartPermission);

        return ChartMapper.toChartDto(savedChart);
    }

    public UserChartPermission getUserChartPermission(Long userId, Long chartId) {
        Optional<ChartPermission> chartPermission = chartPermissionRepository
                .getChartPermissionByChartIdAndUserId(chartId, userId);
        if (chartPermission.isEmpty()) {
            throw new ResourceNotFoundException("Not found chart permission", userId);
        }
        UserDto userDto = userGrpcClient.getUserById(chartPermission.get().getUserId());
        return UserChartPermission.builder()
                .userId(String.valueOf(chartPermission.get().getUserId()))
                .name(userDto.getFirstName() + " " + userDto.getLastName())
                .email(userDto.getEmail())
                .avatar(userDto.getAvatarUrl())
                .permission(ChartPermissionType.fromValue(chartPermission.get().getPermission()))
                .build();
    }

    @Override
    public List<UserChartPermission> getShareChart(Long userId, Long chartId) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        if (Objects.equals(chart.get().getUserId(), userId)) {
            List<ChartPermission> chartPermissions = chartPermissionRepository.getChartPermissionsByChartId(chartId);
            List<UserChartPermission> userChartPermissions = new ArrayList<>();
            for (ChartPermission chartPermission : chartPermissions) {
                if (chartPermission.getPermission().equals(ChartPermissionType.OWNER.getValue())) {
                    continue;
                }
                UserDto userDto = userGrpcClient.getUserById(chartPermission.getUserId());
                userChartPermissions.add(UserChartPermission.builder()
                        .userId(String.valueOf(chartPermission.getUserId()))
                        .name(userDto.getFirstName() + " " + userDto.getLastName())
                        .email(userDto.getEmail())
                        .avatar(userDto.getAvatarUrl())
                        .permission(ChartPermissionType.fromValue(chartPermission.getPermission()))
                        .build());
            }
            return userChartPermissions;
        } else {
            throw new ForbiddenException("You are not allowed to get share chart");
        }
    }

    @Override
    public void updateShareChart(Long userId, Long chartId, ShareChartRequest shareChartRequest) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        if (Objects.equals(chart.get().getUserId(), userId)) {
            chartPermissionRepository.deleteAllChartPermissionsByChartIdAndUserIdNot(chartId, userId);
            for (UserChartPermission userChartPermission : shareChartRequest.getUserChartPermissions()) {
                if (String.valueOf(userId).equals(userChartPermission.getUserId())) {
                    continue;
                }
                ChartPermission chartPermission = ChartPermission.builder()
                        .chartId(chart.get().getId())
                        .userId(Long.parseLong(userChartPermission.getUserId()))
                        .permission(userChartPermission.getPermission().getValue())
                        .build();
                chartPermissionRepository.saveChartPermission(chartPermission);
                updateViewSourcePermissionRelationToChart(Long.parseLong(userChartPermission.getUserId()), chartId);
            }
        } else {
            throw new ForbiddenException("You are not allowed to share this chart");
        }

    }

    private void updateViewSourcePermissionRelationToChart(Long userId, Long chartId) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        ChartDto chartDto = ChartMapper.toChartDto(chart.get());
        Set<String> relatedSourceIds = new HashSet<>();
        QueryOption queryOption = chartDto.getConfig().getQueryOption();
        relatedSourceIds.add(queryOption.getTable());
        List<Join> joins = queryOption.getJoins();
        if (joins != null && !joins.isEmpty()) {
            for (Join join : joins) {
                if (StringUtils.isNullOrEmpty(join.getTable())) {
                    continue;
                }
                relatedSourceIds.add(join.getTable());
            }
        }
        for (String sourceId : relatedSourceIds) {
            Optional<SourcePermission> sourcePermission = sourcePermissionRepository
                    .getSourcePermissionBySourceIdAndUserId(Long.parseLong(sourceId), userId);
            if (sourcePermission.isEmpty()) {
                SourcePermission newSourcePermission = SourcePermission.builder()
                        .sourceId(Long.parseLong(sourceId))
                        .userId(userId)
                        .permission(ChartPermissionType.VIEW.getValue())
                        .build();
                sourcePermissionRepository.saveSourcePermission(newSourcePermission);
            }
        }

    }

    @Override
    public ChartDto cloneChart(Long userId, Long chartId) {
        Optional<Chart> chart = chartRepository.getChartById(chartId);
        if (chart.isEmpty()) {
            throw new ResourceNotFoundException("Chart", chartId);
        }
        if (!Objects.equals(chart.get().getUserId(), userId)
                && !permissionService.hasViewChartPermission(userId, chartId)) {
            throw new ForbiddenException("You are not allowed to clone this chart");
        }
        ChartDto clonedChart = ChartMapper.toChartDto(chart.get());
        clonedChart.setId(String.valueOf(SnowflakeIdGenerator.getInstance().generateId()));
        clonedChart.setName(clonedChart.getName() + " (Copy)");
        clonedChart.setUserId(String.valueOf(userId));
        clonedChart.setCreatedAt(LocalDateTime.now());
        Chart savedChart = chartRepository.save(ChartMapper.toChart(clonedChart));
        if (savedChart == null) {
            throw new RuntimeException("Clone chart failed");
        }

        ChartPermission chartPermission = ChartPermission.builder()
                .chartId(savedChart.getId())
                .userId(userId)
                .permission(ChartPermissionType.OWNER.getValue())
                .build();
        chartPermissionRepository.saveChartPermission(chartPermission);

        return ChartMapper.toChartDto(savedChart);
    }
}

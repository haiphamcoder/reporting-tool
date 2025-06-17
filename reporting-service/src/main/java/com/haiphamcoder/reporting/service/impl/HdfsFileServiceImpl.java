package com.haiphamcoder.reporting.service.impl;

import java.io.InputStream;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.haiphamcoder.reporting.repository.HdfsRepository;
import com.haiphamcoder.reporting.service.HdfsFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HdfsFileServiceImpl implements HdfsFileService {
    private final HdfsRepository hdfsRepository;

    @Override
    public String uploadFile(Long userId, InputStream inputStream, String fileName) {
        return hdfsRepository.uploadFile(userId, inputStream, fileName);
    }

    @Override
    public InputStream streamFile(String fileUrl) {
        return hdfsRepository.streamFile(fileUrl);
    }

    @Override
    public Map<String, String> getHistoryUploadFile(Long userId, Integer connectorType) {
        return hdfsRepository.getHistoryUploadFile(userId, connectorType);
    }

}

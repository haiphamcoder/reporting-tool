package com.haiphamcoder.cdp.application.service;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.domain.repository.HdfsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HdfsFileService {
    private final HdfsRepository hdfsRepository;

    public String uploadFile(String userId, InputStream inputStream, String fileName) {
        return hdfsRepository.uploadFile(userId, inputStream, fileName);
    }

    public InputStream streamFile(String userId, String fileName) {
        return hdfsRepository.streamFile(userId, fileName);
    }
    
}

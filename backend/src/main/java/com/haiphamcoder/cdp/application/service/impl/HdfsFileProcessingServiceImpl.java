package com.haiphamcoder.cdp.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.haiphamcoder.cdp.application.service.HdfsFileProcessingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HdfsFileProcessingServiceImpl implements HdfsFileProcessingService {
    
    @Override
    public List<String> getSchema(String userId, String fileName) {
        return null;
    }

}

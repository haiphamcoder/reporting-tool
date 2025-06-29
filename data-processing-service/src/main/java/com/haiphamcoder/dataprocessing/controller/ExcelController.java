package com.haiphamcoder.dataprocessing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haiphamcoder.dataprocessing.service.ExcelProcessingService;
import com.haiphamcoder.dataprocessing.shared.http.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelProcessingService excelProcessingService;

    @GetMapping("/sheets")
    public ResponseEntity<ApiResponse<Object>> getSheets(
            @CookieValue(value = "user-id", required = true) Long userId,
            @RequestParam(value = "file-path", required = true) String filePath) {
        List<String> sheets = excelProcessingService.getSheets(userId, filePath);
        return ResponseEntity.ok().body(ApiResponse.success(sheets, "Sheets fetched successfully"));
    }
}

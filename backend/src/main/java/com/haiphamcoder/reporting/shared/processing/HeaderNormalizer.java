package com.haiphamcoder.reporting.shared.processing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

/**
 * Utility class to normalize CSV/Excel headers:
 * - Convert to lowercase
 * - Replace spaces and special characters with underscores
 * - Handle multiple consecutive separators
 */
@UtilityClass
public class HeaderNormalizer {

    /**
     * Normalize a single header
     * 
     * @param header The header to normalize
     * @return The normalized header
     */
    public static String normalize(String header) {
        if (header == null || header.trim().isEmpty()) {
            return "";
        }

        // Convert to lowercase and replace unwanted characters with underscores
        String normalized = header.toLowerCase()
                // Replace all spaces and special characters with underscores
                .replaceAll("[^a-z0-9]+", "_")
                // Remove underscores at the beginning and end
                .replaceAll("^_+|_+$", "")
                // Replace multiple underscores with a single underscore
                .replaceAll("_+", "_");

        return normalized;
    }

    /**
     * Normalize a list of headers
     * 
     * @param headers The list of headers to normalize
     * @return The normalized list of headers
     */
    public static List<String> normalizeList(List<String> headers) {
        if (headers == null) {
            return List.of();
        }
        return headers.stream()
                .map(HeaderNormalizer::normalize)
                .collect(Collectors.toList());
    }

    /**
     * Normalize an array of headers
     * 
     * @param headers The array of headers to normalize
     * @return The normalized array of headers
     */
    public static String[] normalizeArray(String[] headers) {
        if (headers == null) {
            return new String[0];
        }
        return Arrays.stream(headers)
                .map(HeaderNormalizer::normalize)
                .toArray(String[]::new);
    }

    // Test method
    public static void main(String[] args) {
        // Test cases
        String[] testHeaders = {
                "First Name",
                "Last_Name",
                "Email Address!!!",
                "Phone@Number",
                "  Multiple   Spaces  ",
                null,
                "",
                "UPPER_CASE_TEST"
        };

        String[] normalized = normalizeArray(testHeaders);
        for (String header : normalized) {
            System.out.println(header);
        }
    }
}

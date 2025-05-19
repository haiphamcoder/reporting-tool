package com.haiphamcoder.storage.shared;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TidbDataTypeDetector {

    public enum DataType {
        INT,
        TINYINT,
        SMALLINT,
        MEDIUMINT,
        BIGINT,
        FLOAT,
        DOUBLE,
        DECIMAL,
        BOOLEAN,
        NUMERIC,
        CHAR,
        VARCHAR,
        BINARY,
        VARBINARY,
        TEXT,
        TINYTEXT,
        MEDIUMTEXT,
        LONGTEXT,
        ENUM,
        SET,
        BLOB,
        DATE,
        TIME,
        DATETIME,
        TIMESTAMP,
        YEAR,
        JSON
    }

    public static DataType detectGeneralizedDataType(String value) {
        if (value == null) {
            return DataType.TEXT;
        }

        value = value.trim();

        // Check for boolean
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return DataType.BOOLEAN;
        }

        // Check for JSON
        if ((value.startsWith("{") && value.endsWith("}")) ||
                (value.startsWith("[") && value.endsWith("]"))) {
            return DataType.JSON;
        }

        // Check for integer - always return BIGINT for generalized type
        try {
            Long.parseLong(value);
            return DataType.BIGINT;
        } catch (NumberFormatException e) {
            // Not an integer, continue checking
        }

        // Check for floating point - always return DOUBLE for generalized type
        try {
            Double.parseDouble(value);
            if (value.contains(".") || value.toLowerCase().contains("e")) {
                return DataType.DOUBLE;
            }
        } catch (NumberFormatException e) {
            // Not a number, continue checking
        }

        // Check for date/time patterns
        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return DataType.DATE;
        }
        if (value.matches("\\d{2}:\\d{2}:\\d{2}")) {
            return DataType.TIME;
        }
        if (value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return DataType.DATETIME;
        }
        if (value.matches("\\d{4}")) {
            return DataType.YEAR;
        }

        return DataType.TEXT;
    }
    
}

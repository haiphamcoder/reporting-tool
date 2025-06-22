package com.haiphamcoder.reporting.domain.dto;

import java.util.List;

import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartConfig;
import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartConfig.AreaChartConfig;
import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartConfig.BarChartConfig;
import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartConfig.LineChartConfig;
import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartConfig.PieChartConfig;
import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartConfig.TableConfig;
import com.haiphamcoder.reporting.domain.dto.ChartDto.ChartConfig.TableConfig.TableColumn;
import com.haiphamcoder.reporting.domain.exception.business.detail.InvalidInputException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChartValidationUtils {

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;
    private static final int MAX_LABEL_LENGTH = 100;
    private static final int MAX_HEADER_LENGTH = 100;

    public static void validateChartDto(ChartDto chartDto) {
        if (chartDto == null) {
            throw new InvalidInputException("Chart data cannot be null");
        }

        validateChartName(chartDto.getName());
        validateChartDescription(chartDto.getDescription());
        validateChartConfig(chartDto.getConfig());
        validateSqlQuery(chartDto.getSqlQuery());
    }

    public static void validateChartName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Chart name is required");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidInputException("Chart name must not exceed " + MAX_NAME_LENGTH + " characters");
        }
    }

    public static void validateChartDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidInputException("Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
    }

    public static void validateChartConfig(ChartConfig config) {
        if (config == null) {
            throw new InvalidInputException("Chart configuration is required");
        }

        if (config.getType() == null) {
            throw new InvalidInputException("Chart type is required");
        }

        // Validate specific config based on chart type
        switch (config.getType()) {
            case BAR:
                validateBarChartConfig(config.getBarChartConfig());
                break;
            case PIE:
                validatePieChartConfig(config.getPieChartConfig());
                break;
            case LINE:
                validateLineChartConfig(config.getLineChartConfig());
                break;
            case AREA:
                validateAreaChartConfig(config.getAreaChartConfig());
                break;
            case TABLE:
                validateTableConfig(config.getTableConfig());
                break;
            default:
                throw new InvalidInputException("Unsupported chart type: " + config.getType());
        }
    }

    public static void validateSqlQuery(String sqlQuery) {
        if (sqlQuery == null || sqlQuery.trim().isEmpty()) {
            throw new InvalidInputException("SQL query is required");
        }
    }

    public static void validateBarChartConfig(BarChartConfig config) {
        if (config == null) {
            throw new InvalidInputException("Bar chart configuration is required for BAR chart type");
        }

        if (config.getXAxis() == null || config.getXAxis().trim().isEmpty()) {
            throw new InvalidInputException("X-axis field is required for bar chart");
        }

        if (config.getYAxis() == null || config.getYAxis().trim().isEmpty()) {
            throw new InvalidInputException("Y-axis field is required for bar chart");
        }

        validateLabel(config.getXAxisLabel(), "X-axis label");
        validateLabel(config.getYAxisLabel(), "Y-axis label");
    }

    public static void validatePieChartConfig(PieChartConfig config) {
        if (config == null) {
            throw new InvalidInputException("Pie chart configuration is required for PIE chart type");
        }

        if (config.getLabelField() == null || config.getLabelField().trim().isEmpty()) {
            throw new InvalidInputException("Label field is required for pie chart");
        }

        if (config.getValueField() == null || config.getValueField().trim().isEmpty()) {
            throw new InvalidInputException("Value field is required for pie chart");
        }
    }

    public static void validateLineChartConfig(LineChartConfig config) {
        if (config == null) {
            throw new InvalidInputException("Line chart configuration is required for LINE chart type");
        }

        if (config.getXAxis() == null || config.getXAxis().trim().isEmpty()) {
            throw new InvalidInputException("X-axis field is required for line chart");
        }

        if (config.getYAxis() == null || config.getYAxis().trim().isEmpty()) {
            throw new InvalidInputException("Y-axis field is required for line chart");
        }

        validateLabel(config.getXAxisLabel(), "X-axis label");
        validateLabel(config.getYAxisLabel(), "Y-axis label");
    }

    public static void validateAreaChartConfig(AreaChartConfig config) {
        if (config == null) {
            throw new InvalidInputException("Area chart configuration is required for AREA chart type");
        }

        if (config.getXAxis() == null || config.getXAxis().trim().isEmpty()) {
            throw new InvalidInputException("X-axis field is required for area chart");
        }

        if (config.getYAxis() == null || config.getYAxis().trim().isEmpty()) {
            throw new InvalidInputException("Y-axis field is required for area chart");
        }

        validateLabel(config.getXAxisLabel(), "X-axis label");
        validateLabel(config.getYAxisLabel(), "Y-axis label");

        if (config.getOpacity() != null && (config.getOpacity() < 0 || config.getOpacity() > 1)) {
            throw new InvalidInputException("Opacity must be between 0 and 1");
        }
    }

    public static void validateTableConfig(TableConfig config) {
        if (config == null) {
            throw new InvalidInputException("Table configuration is required for TABLE chart type");
        }

        if (config.getColumns() == null || config.getColumns().isEmpty()) {
            throw new InvalidInputException("At least one column is required for table");
        }

        for (TableColumn column : config.getColumns()) {
            validateTableColumn(column);
        }

        if (config.getPageSize() != null && config.getPageSize() <= 0) {
            throw new InvalidInputException("Page size must be greater than 0");
        }
    }

    public static void validateTableColumn(TableColumn column) {
        if (column == null) {
            throw new InvalidInputException("Table column cannot be null");
        }

        if (column.getField() == null || column.getField().trim().isEmpty()) {
            throw new InvalidInputException("Column field is required");
        }

        if (column.getHeader() != null && column.getHeader().length() > MAX_HEADER_LENGTH) {
            throw new InvalidInputException("Column header must not exceed " + MAX_HEADER_LENGTH + " characters");
        }
    }

    private static void validateLabel(String label, String labelName) {
        if (label != null && label.length() > MAX_LABEL_LENGTH) {
            throw new InvalidInputException(labelName + " must not exceed " + MAX_LABEL_LENGTH + " characters");
        }
    }

    public static void validateColors(List<String> colors) {
        if (colors != null) {
            for (String color : colors) {
                if (color == null || color.trim().isEmpty()) {
                    throw new InvalidInputException("Color cannot be null or empty");
                }
                // Basic hex color validation
                if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
                    throw new InvalidInputException("Invalid color format: " + color + ". Expected hex format (e.g., #FF0000)");
                }
            }
        }
    }
} 
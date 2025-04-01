package com.haiphamcoder.cdp.shared.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ExcelFileUtils {
    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    public enum ExcelFileFormat {
        XLSX,
        XLS
    }

    private static String getCellValueAsString(Cell cell) {
        return DATA_FORMATTER.formatCellValue(cell);
    }

    private static Workbook createWorkbook(InputStream inputStream, ExcelFileFormat fileFormat) throws IOException {
        switch (fileFormat) {
            case XLSX:
                return new XSSFWorkbook(inputStream);
            case XLS:
                return new HSSFWorkbook(inputStream);
            default:
                throw new IllegalArgumentException("Unsupported file format: " + fileFormat);
        }
    }

    public static List<String> getFieldNames(InputStream inputStream, ExcelFileFormat fileFormat, int sheetIndex, int headerRowIndex) {
        try (Workbook workbook = createWorkbook(inputStream, fileFormat)) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                return Collections.emptyList();
            }
            return splitToList(headerRow);
        } catch (IOException e) {
            log.error("Failed to get field names from Excel file", e);
            throw new UncheckedIOException(e);
        }
    }

    public static List<String> getHeader(InputStream inputStream, String fileName) {
        try (Workbook workbook = getWorkbook(inputStream, fileName)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            Row row = rows.next();
            return splitToList(row);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Stream<List<?>> getRecords(InputStream inputStream, String fileName) {
        try (Workbook workbook = getWorkbook(inputStream, fileName)) {
            Sheet sheet = workbook.getSheetAt(0);
            Spliterator<Row> spliterator = Spliterators.spliteratorUnknownSize(sheet.iterator(), 0);
            return StreamSupport.stream(spliterator, false)
                    .skip(1)
                    .map(ExcelFileUtils::splitToList);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> splitToList(Row row) {
        List<String> values = new LinkedList<>();
        for (Cell cell : row) {
            values.add(getCellValueAsString(cell));
        }
        return values;
    }

    private static Workbook getWorkbook(InputStream inputStream, String fileName) {
        Workbook workbook = null;
        try {
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                throw new IllegalArgumentException("Unsupported file extension: " + fileName);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return workbook;
    }
}

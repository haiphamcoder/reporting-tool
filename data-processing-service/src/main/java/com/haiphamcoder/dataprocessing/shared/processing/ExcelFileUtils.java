package com.haiphamcoder.dataprocessing.shared.processing;

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
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ExcelFileUtils {

    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    /**
     * The format of the Excel file
     */
    public enum ExcelFileFormat {
        XLSX,
        XLS
    }

    /**
     * Process the file selection
     * 
     * @param sheet             the sheet to process
     * @param dataRangeSelected the data range selected
     * @return the options for the file selection
     */
    public static int[] processFileSelection(Sheet sheet, String dataRangeSelected) {
        int[] options = new int[4];
        String locationUpperLeft = "";
        String locationMustUnder = "";
        if (dataRangeSelected != null) {
            String[] optionalRange = dataRangeSelected.split(":");
            locationUpperLeft = optionalRange[0];
            locationMustUnder = optionalRange[1];
        }
        int rowsMaxDefault = sheet.getLastRowNum();
        int colsMinDefault = 0;
        int rowsMinDefault = sheet.getFirstRowNum();
        int colsMaxDefault = 256;
        int rowsMaxSelected = 0;
        int colsMaxSelected = 0;
        int rowsMinSelected = 0;
        int colsMinSelected = 0;
        if (dataRangeSelected != null) {
            rowsMaxSelected = new CellAddress(locationMustUnder).getRow();
            colsMaxSelected = new CellAddress(locationMustUnder).getColumn();
            rowsMinSelected = new CellAddress(locationUpperLeft).getRow();
            colsMinSelected = new CellAddress(locationUpperLeft).getColumn();
        }
        if (dataRangeSelected == null || dataRangeSelected.isEmpty()) {
            options[0] = rowsMinDefault;
            options[1] = colsMinDefault;
            options[2] = rowsMaxDefault;
            options[3] = colsMaxDefault;
        } else {
            if (!locationUpperLeft.isEmpty()) {
                options[0] = Math.max(rowsMinSelected, rowsMinDefault);
                options[1] = Math.max(colsMinSelected, colsMinDefault);
            }
            if (!locationMustUnder.isEmpty()) {
                options[2] = Math.min(rowsMaxSelected, rowsMaxDefault);
                options[3] = Math.min(colsMaxSelected, colsMaxDefault);
            }
        }
        return options;
    }

    public static ExcelFileFormat getFileFormat(String fileName) {
        if (fileName.endsWith(".xlsx")) {
            return ExcelFileFormat.XLSX;
        } else if (fileName.endsWith(".xls")) {
            return ExcelFileFormat.XLS;
        }
        throw new IllegalArgumentException("Unsupported file extension: " + fileName);
    }

    /**
     * Get the value of a cell as a string
     * 
     * @param cell      the cell to get the value from
     * @param evaluator the evaluator to use to evaluate the cell
     * @return the value of the cell as a string
     */
    public static String getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
        return DATA_FORMATTER.formatCellValue(cell, evaluator);
    }

    /**
     * Get the value of a cell as a string
     * 
     * @param cell the cell to get the value from
     * @return the value of the cell as a string
     */
    public static String getCellValueAsString(Cell cell) {
        return DATA_FORMATTER.formatCellValue(cell);
    }

    /**
     * Create a workbook from an input stream
     * 
     * @param inputStream the input stream to create the workbook from
     * @param fileFormat  the format of the workbook
     * @return the workbook created from the input stream
     */
    public static Workbook createWorkbook(InputStream inputStream, ExcelFileFormat fileFormat) throws IOException {
        switch (fileFormat) {
            case XLSX:
                return new XSSFWorkbook(inputStream);
            case XLS:
                return new HSSFWorkbook(inputStream);
            default:
                throw new IllegalArgumentException("Unsupported file format: " + fileFormat);
        }
    }

    /**
     * Get the sheets from an Excel file
     * 
     * @param inputStream the input stream to get the sheets from
     * @param fileFormat  the format of the workbook
     * @return the sheets from the Excel file
     */
    public static List<String> getSheets(InputStream inputStream, ExcelFileFormat fileFormat) {
        List<String> sheets = new LinkedList<>();
        try (Workbook workbook = createWorkbook(inputStream, fileFormat)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheets.add(workbook.getSheetName(i));
            }
            return sheets;
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Get the field names from an Excel file
     * 
     * @param inputStream    the input stream to get the field names from
     * @param fileFormat     the format of the workbook
     * @param sheetName      the name of the sheet to get the field names from
     * @param headerRowIndex the index of the row to get the field names from
     * @return the field names from the Excel file
     */
    public static List<String> getFieldNames(InputStream inputStream, ExcelFileFormat fileFormat, String sheetName,
            int headerRowIndex) {
        try (Workbook workbook = createWorkbook(inputStream, fileFormat)) {
            Sheet sheet = workbook.getSheet(sheetName);
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

    /**
     * Get the header from an Excel file
     * 
     * @param inputStream the input stream to get the header from
     * @param fileFormat  the format of the workbook
     * @param sheetName   the name of the sheet to get the header from
     * @return the header from the Excel file
     */
    public static List<String> getHeader(InputStream inputStream, ExcelFileFormat fileFormat, String sheetName,
            String dataRangeSelected) {
        List<String> header = new LinkedList<>();
        try (Workbook workbook = createWorkbook(inputStream, fileFormat)) {
            Sheet sheet = workbook.getSheet(sheetName);
            int[] options = processFileSelection(sheet, dataRangeSelected);
            int rowsMin = options[0];
            int colsMin = options[1];
            int rowsMax = options[2];
            int colsMax = options[3];
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext() && header.isEmpty()) {
                Row row = rowIterator.next();
                if (row.getRowNum() < rowsMin || row.getRowNum() > rowsMax) {
                    continue;
                }
                for (int i = colsMin; i < colsMax; i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null) {
                        header.add(null);
                        continue;
                    }
                    header.add(getCellValueAsString(cell));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return header;
    }

    /**
     * Get the records from an Excel file
     * 
     * @param inputStream the input stream to get the records from
     * @param fileFormat  the format of the workbook
     * @param sheetName   the name of the sheet to get the records from
     * @return the records from the Excel file
     */
    public static Stream<List<?>> getRecords(InputStream inputStream, ExcelFileFormat fileFormat, String sheetName) {
        try (Workbook workbook = createWorkbook(inputStream, fileFormat)) {
            Sheet sheet = workbook.getSheet(sheetName);
            Spliterator<Row> spliterator = Spliterators.spliteratorUnknownSize(sheet.iterator(), 0);
            return StreamSupport.stream(spliterator, false)
                    .skip(1)
                    .map(ExcelFileUtils::splitToList);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Split a row into a list of strings
     * 
     * @param row the row to split
     * @return the list of strings
     */
    public static List<String> splitToList(Row row) {
        List<String> values = new LinkedList<>();
        for (Cell cell : row) {
            values.add(getCellValueAsString(cell));
        }
        return values;
    }
}

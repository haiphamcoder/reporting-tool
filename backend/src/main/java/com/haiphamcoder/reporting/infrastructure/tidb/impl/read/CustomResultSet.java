package com.haiphamcoder.reporting.infrastructure.tidb.impl.read;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomResultSet {

    @JsonProperty("meta_data")
    private final MetaData metaData;

    @JsonProperty("rows")
    private final List<Map<String, Object>> rows;

    @JsonProperty("index")
    private int index;

    @JsonProperty("runtime")
    private long runtime;

    public CustomResultSet(long runtime) {
        this.metaData = new MetaData();
        this.rows = new LinkedList<>();
        this.runtime = runtime;
    }

    public CustomResultSet(ResultSet resultSet, long runtime) throws SQLException {
        this.metaData = new MetaData(resultSet.getMetaData());
        this.index = -1;
        this.rows = new ArrayList<>();
        while (resultSet.next()) {
            final Map<String, Object> row = new HashMap<>(metaData.getColumnCount(), 1.0f);
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                final String column = metaData.getColumnLabel(i);
                Object value = resultSet.getObject(i);
                row.put(column, value);
            }
            rows.add(row);
        }
        resultSet.close();
        this.runtime = runtime;
    }

    public long getLong(String columnLabel) {
        return Long.parseLong(rows.get(index).get(columnLabel).toString());
    }

    public Object getObject(String columnLabel) {
        return rows.get(index).get(columnLabel);
    }

    public boolean next() {
        index++;
        return index < rows.size();
    }

    public void resetIndex() {
        index = -1;
    }

    public CustomResultSet copy() {
        return new CustomResultSet(
                metaData.copy(),
                rows,
                index,
                runtime);
    }

    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaData {

        @JsonProperty("column_count")
        private final int columnCount;

        @JsonProperty("column_labels")
        private final String[] columnLabels;

        @JsonProperty("column_type_names")
        private final String[] columnTypeNames;

        public MetaData() {
            this.columnCount = 0;
            this.columnLabels = new String[0];
            this.columnTypeNames = new String[0];
        }

        public MetaData(ResultSetMetaData resultSetMetaData) throws SQLException {
            columnCount = resultSetMetaData.getColumnCount();
            columnLabels = new String[columnCount];
            columnTypeNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnLabels[i - 1] = resultSetMetaData.getColumnLabel(i);
                columnTypeNames[i - 1] = resultSetMetaData.getColumnTypeName(i);
            }
        }

        public String getColumnLabel(int i) {
            return columnLabels[i - 1];
        }

        public String getColumnTypeName(int i) {
            return columnTypeNames[i - 1];
        }

        public MetaData copy() {
            return new MetaData(
                    columnCount,
                    columnLabels,
                    columnTypeNames);
        }
    }
}

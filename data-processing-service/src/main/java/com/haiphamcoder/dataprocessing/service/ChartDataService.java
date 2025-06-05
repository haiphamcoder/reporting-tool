package com.haiphamcoder.dataprocessing.service;

import java.util.List;
import java.util.Map;

public interface ChartDataService {
    /**
     * Lấy dữ liệu của biểu đồ từ bảng chart_{chartId}
     * 
     * @param chartId Id của biểu đồ
     * @return Dữ liệu cho biểu đồ
     */
    Map<String, Object> getChartData(Long chartId);

    /**
     * Lấy dữ liệu cho nhiều biểu đồ
     * 
     * @param chartIds Danh sách id của các biểu đồ
     * @return Dữ liệu cho các biểu đồ
     */
    List<Map<String, Object>> getChartsData(List<Long> chartIds);

    /**
     * Kiểm tra xem dữ liệu của biểu đồ có tồn tại không
     * 
     * @param chartId Id của biểu đồ
     * @return true nếu dữ liệu tồn tại, false nếu không
     */
    boolean hasChartData(Long chartId);

    /**
     * Lấy thời gian cập nhật dữ liệu gần nhất của biểu đồ
     * 
     * @param chartId Id của biểu đồ
     * @return Thời gian cập nhật gần nhất
     */
    String getLastUpdateTime(Long chartId);
}

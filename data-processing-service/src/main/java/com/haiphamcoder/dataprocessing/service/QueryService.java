package com.haiphamcoder.dataprocessing.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.haiphamcoder.dataprocessing.domain.model.ChartSchedule;

public interface QueryService {
    /**
     * Thực thi truy vấn dựa trên các tùy chọn và lưu kết quả vào bảng chart_{chartId}
     * 
     * @param chartId ID của biểu đồ
     * @param queryOption Các tùy chọn truy vấn
     * @return true nếu thực thi thành công, false nếu thất bại
     */
    boolean executeAndSaveQuery(Long chartId, JsonNode queryOption);

    /**
     * Lên lịch thực thi truy vấn cho một biểu đồ
     * 
     * @param chartId ID của biểu đồ
     * @param cronExpression Biểu thức cron cho lịch thực thi
     * @return Thông tin về lịch thực thi
     */
    ChartSchedule scheduleQuery(Long chartId, String cronExpression);

    /**
     * Cập nhật lịch thực thi truy vấn cho một biểu đồ
     * 
     * @param chartId ID của biểu đồ
     * @param cronExpression Biểu thức cron mới
     * @return Thông tin về lịch thực thi đã cập nhật
     */
    ChartSchedule updateSchedule(Long chartId, String cronExpression);

    /**
     * Vô hiệu hóa lịch thực thi truy vấn cho một biểu đồ
     * 
     * @param chartId ID của biểu đồ
     */
    void disableSchedule(Long chartId);

    /**
     * Kích hoạt lịch thực thi truy vấn cho một biểu đồ
     * 
     * @param chartId ID của biểu đồ
     */
    void enableSchedule(Long chartId);

    /**
     * Lấy thông tin về lịch thực thi của một biểu đồ
     * 
     * @param chartId ID của biểu đồ
     * @return Thông tin về lịch thực thi
     */
    ChartSchedule getSchedule(Long chartId);

    /**
     * Lấy danh sách tất cả các lịch thực thi đang hoạt động
     * 
     * @return Danh sách các lịch thực thi
     */
    List<ChartSchedule> getAllActiveSchedules();

    /**
     * Kiểm tra tính hợp lệ của truy vấn
     * 
     * @param queryOption Các tùy chọn truy vấn
     * @return true nếu truy vấn hợp lệ, false nếu không
     */
    boolean validateQuery(JsonNode queryOption);

    /**
     * Tối ưu hóa truy vấn
     * 
     * @param queryOption Các tùy chọn truy vấn
     * @return Các tùy chọn truy vấn đã được tối ưu hóa
     */
    JsonNode optimizeQuery(JsonNode queryOption);
}

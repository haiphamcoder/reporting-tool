CREATE DATABASE IF NOT EXISTS `reporting_tool`;

USE `reporting_tool`;

DROP TABLE IF EXISTS chart;

CREATE TABLE
    chart (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID duy nhất cho mỗi chart',
        name VARCHAR(255) NOT NULL COMMENT 'Tên của chart',
        description VARCHAR(255) COMMENT 'Mô tả của chart',
        config TEXT NOT NULL COMMENT 'Cấu hình của chart',
        query_option TEXT NOT NULL COMMENT 'Cấu hình của chart',
        user_id BIGINT NOT NULL COMMENT 'ID của user',
        is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trạng thái xóa của chart',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản',
        modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật gần nhất',
        CONSTRAINT fk_chart_user FOREIGN KEY (user_id) REFERENCES users (id)
    );

CREATE INDEX idx_chart_name ON chart (name);

CREATE INDEX idx_chart_description ON chart (description);

CREATE INDEX idx_chart_user_id ON chart (user_id);

CREATE INDEX idx_chart_is_deleted ON chart (is_deleted);
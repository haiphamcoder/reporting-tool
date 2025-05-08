CREATE DATABASE IF NOT EXISTS `reporting_tool`;

USE `reporting_tool`;

DROP TABLE IF EXISTS report;

CREATE TABLE
    report (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID duy nhất cho mỗi report',
        name VARCHAR(255) NOT NULL COMMENT 'Tên của report',
        description VARCHAR(255) COMMENT 'Mô tả của report',
        config TEXT NOT NULL COMMENT 'Cấu hình của report',
        user_id BIGINT NOT NULL COMMENT 'ID của user',
        is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trạng thái xóa của report',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản',
        modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật gần nhất',
        CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users (id)
    );

CREATE INDEX idx_report_name ON report (name);

CREATE INDEX idx_report_description ON report (description);

CREATE INDEX idx_report_user_id ON report (user_id);

CREATE INDEX idx_report_is_deleted ON report (is_deleted);
CREATE DATABASE IF NOT EXISTS `reporting_tool`;

USE `reporting_tool`;

DROP TABLE IF EXISTS report;

CREATE TABLE
    report (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID duy nhất cho mỗi report',
        name VARCHAR(255) NOT NULL COMMENT 'Tên của report',
        description VARCHAR(255) COMMENT 'Mô tả của report',
        user_id BIGINT NOT NULL COMMENT 'ID của user',
        config TEXT NOT NULL COMMENT 'Cấu hình của chart',
        is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trạng thái xóa của report',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản',
        modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật gần nhất',
        CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users (id)
    );

CREATE TABLE
    report_permission (
        report_id BIGINT NOT NULL COMMENT 'ID của report',
        user_id BIGINT NOT NULL COMMENT 'ID của user',
        permission VARCHAR(255) NOT NULL COMMENT 'Quyền của user',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo source',
        modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật gần nhất',
        CONSTRAINT fk_report_permission_report FOREIGN KEY (report_id) REFERENCES report (id),
        CONSTRAINT fk_report_permission_user FOREIGN KEY (user_id) REFERENCES users (id),
        PRIMARY KEY (report_id, user_id)
    );

CREATE INDEX idx_report_name ON report (name);

CREATE INDEX idx_report_description ON report (description);

CREATE INDEX idx_report_user_id ON report (user_id);

CREATE INDEX idx_report_is_deleted ON report (is_deleted);

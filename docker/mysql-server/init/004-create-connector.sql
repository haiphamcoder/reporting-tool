CREATE DATABASE IF NOT EXISTS `reporting_tool`;

USE `reporting_tool`;

DROP TABLE IF EXISTS connector;

CREATE TABLE connector (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID duy nhất cho mỗi connector',
    name VARCHAR(255) NOT NULL COMMENT 'Tên của connector',
    description TEXT COMMENT 'Mô tả của connector',
    logo TEXT COMMENT 'Logo của connector',
    type INT NOT NULL COMMENT 'Loại của connector',
    category ENUM ('FILE', 'DATABASE') NOT NULL DEFAULT 'FILE' COMMENT 'Nhóm của connector',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Trạng thái của connector',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo connector',
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật connector',
    CONSTRAINT category_check CHECK (category in ('FILE', 'DATABASE'))
);

INSERT INTO connector (id, name, description, type, category) VALUES
(1, 'CSV', 'Connector for CSV files', 1, 'FILE'),
(2, 'Excel', 'Connector for Excel files', 1, 'FILE');


CREATE DATABASE IF NOT EXISTS `reporting_tool`;

USE `reporting_tool`;

DROP TABLE IF EXISTS source;

CREATE TABLE
    source (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID duy nhất cho mỗi source',
        name VARCHAR(255) NOT NULL COMMENT 'Tên của source',
        description VARCHAR(255) COMMENT 'Mô tả của source',
        connector_type INT NOT NULL COMMENT 'Loại connector của source',
        mapping TEXT COMMENT 'Schema của source',
        config TEXT COMMENT 'Config của source',
        table_name VARCHAR(255) COMMENT 'Tên bảng của source',
        status INT NOT NULL COMMENT 'Trạng thái của source',
        user_id BIGINT NOT NULL COMMENT 'ID của user',
        is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trạng thái xóa của source',
        is_starred BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trạng thái yêu thích của source',
        last_sync_time TIMESTAMP NULL COMMENT 'Thời gian cuối cùng sync của source',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản',
        modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật gần nhất',
        CONSTRAINT fk_source_user FOREIGN KEY (user_id) REFERENCES users (id),
        CONSTRAINT fk_source_folder FOREIGN KEY (folder_id) REFERENCES folder (id)
    );

DROP TABLE IF EXISTS source_permission;

CREATE TABLE
    source_permission (
        source_id BIGINT NOT NULL COMMENT 'ID của source',
        user_id BIGINT NOT NULL COMMENT 'ID của user',
        permission VARCHAR(255) NOT NULL COMMENT 'Quyền của user',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo source',
        modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật gần nhất',
        CONSTRAINT fk_source_permission_source FOREIGN KEY (source_id) REFERENCES source (id),
        CONSTRAINT fk_source_permission_user FOREIGN KEY (user_id) REFERENCES users (id),
        PRIMARY KEY (source_id, user_id)
    );

CREATE INDEX idx_source_name ON source (name);

CREATE INDEX idx_source_description ON source (description);

CREATE INDEX idx_source_user_id ON source (user_id);

CREATE INDEX idx_source_folder_id ON source (folder_id);

CREATE INDEX idx_source_type_connector ON source (connector_type);

CREATE INDEX idx_source_status ON source (status);

CREATE INDEX idx_source_is_deleted ON source (is_deleted);

CREATE INDEX idx_source_is_starred ON source (is_starred);
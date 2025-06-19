CREATE DATABASE IF NOT EXISTS `reporting_tool`;

USE `reporting_tool`;

DROP TABLE IF EXISTS users;

CREATE TABLE
    users (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID duy nhất cho mỗi user',
        first_name VARCHAR(100) NOT NULL COMMENT 'Tên của user',
        last_name VARCHAR(100) COMMENT 'Họ của user',
        username VARCHAR(255) NOT NULL UNIQUE COMMENT 'Tên đăng nhập (duy nhất)',
        email VARCHAR(255) NOT NULL UNIQUE COMMENT 'Email của user (duy nhất)',
        email_verified BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trạng thái email: TRUE = Đã xác nhận, FALSE = Chưa xác nhận',
        provider VARCHAR(255) NOT NULL DEFAULT 'local' COMMENT 'Nguồn cung cấp tài khoản',
        provider_id VARCHAR(255) COMMENT 'ID của user trên nguồn cung cấp',
        avatar_url VARCHAR(255) COMMENT 'Link ảnh đại diện của user',
        password VARCHAR(255) NOT NULL COMMENT 'Mật khẩu (được hash trước khi lưu)',
        role VARCHAR(255) NOT NULL DEFAULT 'user' COMMENT 'Vai trò của user: admin, manager, user',
        first_login BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trạng thái đăng nhập lần đầu: TRUE = Lần đầu, FALSE = Không phải lần đầu',
        enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Trạng thái tài khoản: TRUE = Hoạt động, FALSE = Bị khóa',
        deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Xóa mềm: TRUE = Đã xóa, FALSE = Hoạt động',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản',
        modified_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật gần nhất'
    ) COMMENT = 'Bảng lưu thông tin người dùng trong hệ thống';

DROP TABLE IF EXISTS refresh_tokens;

CREATE TABLE
    refresh_tokens (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID duy nhất của refresh token',
        user_id BIGINT NOT NULL COMMENT 'ID của user sở hữu token',
        token VARCHAR(512) NOT NULL UNIQUE COMMENT 'JWT của refresh_token',
        type ENUM ('BEARER', 'MAC', 'OTHER') NOT NULL DEFAULT 'BEARER' COMMENT 'Loại refresh token',
        expired_at DATETIME NOT NULL COMMENT 'Thời gian hết hạn của refresh token',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo refresh token',
        CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    ) COMMENT = 'Bảng lưu refresh token để xác thực user khi access token hết hạn';

DROP TABLE IF EXISTS otp;

CREATE TABLE
    otp (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID duy nhất của OTP',
        user_id BIGINT NOT NULL COMMENT 'ID của user nhận OTP',
        otp_code VARCHAR(10) NOT NULL COMMENT 'Mã OTP',
        expired_at DATETIME NOT NULL COMMENT 'Thời gian hết hạn của OTP',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo OTP',
        verified BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Trạng thái OTP: TRUE = Đã xác nhận, FALSE = Chưa xác nhận',
        CONSTRAINT fk_otp_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    ) COMMENT = 'Bảng lưu mã OTP dùng để xác thực user';

CREATE INDEX idx_user_id ON refresh_tokens (user_id);

CREATE INDEX idx_otp_user ON otp (user_id);
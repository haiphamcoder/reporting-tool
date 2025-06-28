-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    timestamp DATETIME NOT NULL,
    `read` BOOLEAN NOT NULL DEFAULT FALSE,
    action_url VARCHAR(500),
    user_id VARCHAR(100),
    INDEX idx_user_id (user_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_read (read),
    INDEX idx_user_read (user_id, read)
);

-- Insert sample data for testing
INSERT INTO notifications (type, category, title, message, timestamp, `read`, action_url, user_id) VALUES
('SUCCESS', 'REPORT', 'Report Generated', 'Your monthly report has been generated successfully', NOW() - INTERVAL 1 HOUR, false, '/reports/monthly', 'default-user'),
('INFO', 'SYSTEM', 'System Maintenance', 'Scheduled maintenance will occur tonight at 2 AM', NOW() - INTERVAL 2 HOUR, false, '/maintenance', 'default-user'),
('WARNING', 'CHART', 'Chart Update Required', 'Some charts need to be updated with latest data', NOW() - INTERVAL 3 HOUR, true, '/charts/update', 'default-user'),
('ERROR', 'SOURCE', 'Data Source Error', 'Failed to connect to external data source', NOW() - INTERVAL 4 HOUR, false, '/sources/troubleshoot', 'default-user'),
('SUCCESS', 'USER', 'Profile Updated', 'Your profile has been updated successfully', NOW() - INTERVAL 5 HOUR, true, '/profile', 'default-user'); 
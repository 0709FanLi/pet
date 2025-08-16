-- 创建宠物侦探申请信息缓存表
USE pet_recovery;

CREATE TABLE IF NOT EXISTS detective_application_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    real_name VARCHAR(100),
    phone VARCHAR(20),
    city VARCHAR(100),
    company_name VARCHAR(200),
    address VARCHAR(500),
    team_size INT,
    devices JSON,
    device_photos JSON,
    experience_years INT,
    service_areas JSON,
    available_times JSON,
    bio TEXT,
    id_card_front VARCHAR(255),
    id_card_back VARCHAR(255),
    certificates JSON,
    payout_type VARCHAR(50),
    payout_account VARCHAR(200),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 创建索引
    INDEX idx_user_id (user_id),
    INDEX idx_updated_at (updated_at),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物侦探申请信息缓存表';

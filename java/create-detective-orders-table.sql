-- 宠物侦探订单表
USE pet_recovery;

CREATE TABLE IF NOT EXISTS detective_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lost_pet_id BIGINT NOT NULL COMMENT '关联寻宠启事ID',
    detective_id BIGINT NOT NULL COMMENT '侦探用户ID', 
    status VARCHAR(20) NOT NULL DEFAULT 'intention' COMMENT '订单状态：intention/confirmed/in_progress/completed/closed/withdrawn',
    intention_at TIMESTAMP NULL COMMENT '表达意向时间',
    confirmed_at TIMESTAMP NULL COMMENT '确认接单时间',
    estimated_completion VARCHAR(50) NULL COMMENT '预计完成时间',
    service_description TEXT NULL COMMENT '服务说明（意向时填写）',
    progress_updates JSON NULL COMMENT '进度更新记录',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    withdrawn_at TIMESTAMP NULL COMMENT '撤回时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 外键约束
    FOREIGN KEY (lost_pet_id) REFERENCES lost_pets(id) ON DELETE CASCADE,
    FOREIGN KEY (detective_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- 索引
    INDEX idx_lost_pet_id (lost_pet_id),
    INDEX idx_detective_id (detective_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    
    -- 唯一约束：同一侦探对同一订单只能有一条记录
    UNIQUE KEY uk_detective_pet (detective_id, lost_pet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='侦探订单表';

-- 消息通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(255) NOT NULL COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    type VARCHAR(50) NOT NULL COMMENT '消息类型：order/system/recommend/announcement',
    related_id BIGINT NULL COMMENT '关联业务ID（如订单ID）',
    status VARCHAR(20) NOT NULL DEFAULT 'unread' COMMENT '消息状态：unread/read/deleted',
    priority VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '优先级：low/normal/high/urgent',
    extra_data JSON NULL COMMENT '扩展数据',
    expires_at TIMESTAMP NULL COMMENT '过期时间',
    read_at TIMESTAMP NULL COMMENT '读取时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- 索引
    INDEX idx_user_status_created (user_id, status, created_at),
    INDEX idx_type_created (type, created_at),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';

-- 用户通知设置表
CREATE TABLE IF NOT EXISTS notification_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_notifications BOOLEAN DEFAULT TRUE COMMENT '订单通知开关',
    system_notifications BOOLEAN DEFAULT TRUE COMMENT '系统通知开关',
    recommend_notifications BOOLEAN DEFAULT TRUE COMMENT '推荐通知开关',
    quiet_start_time TIME DEFAULT '22:00:00' COMMENT '勿扰开始时间',
    quiet_end_time TIME DEFAULT '08:00:00' COMMENT '勿扰结束时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- 唯一约束
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户通知设置表';

-- 插入默认通知设置（为现有用户）
INSERT INTO notification_settings (user_id)
SELECT id FROM users 
WHERE id NOT IN (SELECT user_id FROM notification_settings);

COMMIT;

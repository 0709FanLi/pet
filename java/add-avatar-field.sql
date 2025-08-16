-- 为用户表添加头像字段
-- 如果字段不存在则添加

-- 检查字段是否存在并添加
SET @exist = (SELECT count(*) FROM information_schema.COLUMNS WHERE 
  TABLE_SCHEMA = DATABASE() AND 
  TABLE_NAME = 'users' AND 
  COLUMN_NAME = 'avatar');

SET @sqlstmt = IF(@exist > 0, 
  'SELECT "字段已存在，无需添加"', 
  'ALTER TABLE users ADD COLUMN avatar VARCHAR(255) DEFAULT NULL COMMENT "用户头像路径"');

PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 显示表结构确认
DESCRIBE users;

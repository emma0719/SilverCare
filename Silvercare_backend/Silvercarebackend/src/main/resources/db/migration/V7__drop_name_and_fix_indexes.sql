-- V7__drop_name_and_fix_indexes.sql

-- 让脚本对当前数据库名自适应
SET @db := DATABASE();

-- 1) 只在 users.name 存在时删除它
SET @col_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME = 'name'
);
SET @sql := IF(@col_exists > 0,
               'ALTER TABLE users DROP COLUMN name',
               'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) 只在重复唯一索引 `username` 存在时删除它（保留 ux_users_username）
SET @idx_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'users'
    AND INDEX_NAME = 'username'
);
SET @sql := IF(@idx_exists > 0,
               'ALTER TABLE users DROP INDEX `username`',
               'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

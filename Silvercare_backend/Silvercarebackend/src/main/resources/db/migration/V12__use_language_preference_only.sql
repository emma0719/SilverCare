-- 1) 先把旧列的值搬到新列（有值就覆盖，没有就保持原值/默认）
UPDATE users
SET language_preference = COALESCE(preferred_language, language_preference, 'en');

-- 2) 安全地删除旧列（用 information_schema 检查是否存在）
SET @col_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME = 'preferred_language'
);

SET @sql := IF(@col_exists > 0,
  'ALTER TABLE users DROP COLUMN preferred_language',
  'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3) 最终收口：确保新列非空 + 默认值（按你的实体要求）
ALTER TABLE users
    MODIFY COLUMN language_preference VARCHAR(10) NOT NULL DEFAULT 'en';

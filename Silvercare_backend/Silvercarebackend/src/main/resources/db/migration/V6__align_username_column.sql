-- 对齐登录名到 users.username，并移除旧的 name 列
-- 采用 information_schema + PREPARE 动态执行，兼容不支持 IF [NOT] EXISTS 的版本

-- 0) 若无 username 列则新增（先允许 NULL，后面再改 NOT NULL）
SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'username'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE users ADD COLUMN username VARCHAR(50) NULL',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 1) 回填：当 username 为空/NULL 时，用旧列 name 的值
UPDATE users
SET username = name
WHERE (username IS NULL OR username = '')
  AND name IS NOT NULL AND name <> '';

-- 1b) 兜底：仍为空的给一个唯一占位（确保后续 NOT NULL 不失败）
UPDATE users
SET username = CONCAT('user_', id)
WHERE username IS NULL OR username = '';

-- 2) 去掉首尾空格，避免唯一索引冲突
UPDATE users
SET username = TRIM(username)
WHERE username IS NOT NULL;

-- 3) 确保 language_preference 存在；若不存在则新增为 NOT NULL DEFAULT 'en'
SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'language_preference'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE users ADD COLUMN language_preference VARCHAR(10) NOT NULL DEFAULT ''en''' ,
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3b) 把已有空值补成 'en'
UPDATE users
SET language_preference = 'en'
WHERE language_preference IS NULL OR language_preference = '';

-- 4) 为 username 创建唯一索引（若不存在）
SET @idx_exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND index_name = 'ux_users_username'
);
SET @sql := IF(@idx_exists = 0,
  'CREATE UNIQUE INDEX ux_users_username ON users (username)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 如果这里因为重复用户名报错，请先用：
-- SELECT username, COUNT(*) c FROM users GROUP BY username HAVING c > 1;
-- 处理重复后重试迁移。

-- 5) 把 username 改为 NOT NULL
ALTER TABLE users
    MODIFY COLUMN username VARCHAR(50) NOT NULL;

-- 6) 删除旧列 name（若存在）
SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'name'
);
SET @sql := IF(@col_exists = 1,
  'ALTER TABLE users DROP COLUMN name',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
USE silvercare;
SELECT id, username, email, full_name, role, active, created_at
FROM users
ORDER BY id DESC
    LIMIT 5;


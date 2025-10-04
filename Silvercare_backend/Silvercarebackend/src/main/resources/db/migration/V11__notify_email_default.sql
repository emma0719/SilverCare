-- 如果是 tinyint(1) 布尔开关
ALTER TABLE users
    MODIFY notify_email TINYINT(1) NOT NULL DEFAULT 1; -- 或 DEFAULT 0

-- 清理历史脏数据（如果存在 null）
UPDATE users SET notify_email = COALESCE(notify_email, 1);

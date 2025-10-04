-- 扩展 users 表，新增个人资料、通知、安全与偏好相关字段
ALTER TABLE users
    ADD COLUMN avatar_url           VARCHAR(255) NULL AFTER email,
    ADD COLUMN date_of_birth        DATE NULL AFTER avatar_url,
    ADD COLUMN timezone             VARCHAR(50) NULL AFTER date_of_birth,
    ADD COLUMN country              VARCHAR(64) NULL AFTER timezone,
    ADD COLUMN city                 VARCHAR(64) NULL AFTER country,
    ADD COLUMN preferred_units      VARCHAR(16) NOT NULL DEFAULT 'METRIC' AFTER city, -- METRIC/IMPERIAL
    ADD COLUMN theme                VARCHAR(16) NOT NULL DEFAULT 'LIGHT' AFTER preferred_units, -- LIGHT/DARK/SYSTEM
    ADD COLUMN job_title            VARCHAR(100) NULL AFTER theme,
    ADD COLUMN organization         VARCHAR(120) NULL AFTER job_title,
    ADD COLUMN notify_email         TINYINT(1) NOT NULL DEFAULT 1 AFTER organization,
    ADD COLUMN notify_sms           TINYINT(1) NOT NULL DEFAULT 0 AFTER notify_email,
    ADD COLUMN notify_push          TINYINT(1) NOT NULL DEFAULT 1 AFTER notify_sms,
    ADD COLUMN two_factor_enabled   TINYINT(1) NOT NULL DEFAULT 0 AFTER notify_push,
    ADD COLUMN last_login_at        DATETIME NULL AFTER updated_at;

-- 可选：为 timezone / country 建索引以便统计（并非必须）
-- CREATE INDEX idx_users_timezone ON users(timezone);
-- CREATE INDEX idx_users_country  ON users(country);

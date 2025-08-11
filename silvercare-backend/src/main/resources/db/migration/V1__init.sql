CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(191) NOT NULL,
                       email VARCHAR(191) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(32) NOT NULL DEFAULT 'CAREGIVER',
                       created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE care_recipients (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 name VARCHAR(191) NOT NULL,
                                 gender VARCHAR(16),
                                 dob DATE,
                                 room_no VARCHAR(64),
                                 allergies TEXT,
                                 conditions TEXT,
                                 created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                                 PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO users(name,email,password_hash,role)
VALUES ('Admin','admin@silvercare.local','$2a$10$devhash','ADMIN');

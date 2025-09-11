-- users
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- care recipients
CREATE TABLE care_recipients (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 full_name VARCHAR(100) NOT NULL,
                                 phone_number VARCHAR(20),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- join table
CREATE TABLE user_care_recipient (
                                     user_id BIGINT NOT NULL,
                                     care_recipient_id BIGINT NOT NULL,
                                     PRIMARY KEY (user_id, care_recipient_id),
                                     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                     CONSTRAINT fk_care_recipient FOREIGN KEY (care_recipient_id) REFERENCES care_recipients(id) ON DELETE CASCADE
);

-- reminders
CREATE TABLE medication_reminders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      care_recipient_id BIGINT NOT NULL,
                                      med_title VARCHAR(255) NOT NULL,
                                      repeat_type VARCHAR(50),
                                      start_date DATE,
                                      end_date DATE,
                                      time_points JSON,
                                      active BOOLEAN DEFAULT TRUE,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_reminder_recipient FOREIGN KEY (care_recipient_id) REFERENCES care_recipients(id) ON DELETE CASCADE
);

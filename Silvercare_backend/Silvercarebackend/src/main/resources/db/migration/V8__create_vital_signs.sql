CREATE TABLE vital_signs (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             temperature DOUBLE,
                             heart_rate INT,
                             respiratory_rate INT,
                             systolic_bp INT,
                             diastolic_bp INT,
                             spo2 INT,
                             weight DOUBLE,
                             blood_glucose DOUBLE,
                             pain_level INT,
                             care_recipient_id BIGINT NOT NULL,
                             recorded_by_user_id BIGINT,
                             recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_vital_sign_recipient FOREIGN KEY (care_recipient_id) REFERENCES care_recipients(id),
                             CONSTRAINT fk_vital_sign_user FOREIGN KEY (recorded_by_user_id) REFERENCES users(id)
);

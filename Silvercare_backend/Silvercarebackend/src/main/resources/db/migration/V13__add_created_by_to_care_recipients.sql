ALTER TABLE care_recipients
    ADD COLUMN created_by_id BIGINT NULL;

ALTER TABLE care_recipients
    ADD CONSTRAINT fk_care_recipient_creator
        FOREIGN KEY (created_by_id)
            REFERENCES users(id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;
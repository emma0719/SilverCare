
ALTER TABLE care_recipients
    ADD COLUMN family_id BIGINT;


ALTER TABLE care_recipients
    ADD CONSTRAINT fk_care_recipients_family
        FOREIGN KEY (family_id)
            REFERENCES users(id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;


ALTER TABLE care_recipients
    MODIFY family_id BIGINT COMMENT 'Family user who manages this care recipient';

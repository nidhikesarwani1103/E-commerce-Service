ALTER TABLE payments
    ADD expires_at datetime NULL;

ALTER TABLE payments
DROP
COLUMN order_id;

ALTER TABLE payments
    ADD order_id BIGINT NULL;
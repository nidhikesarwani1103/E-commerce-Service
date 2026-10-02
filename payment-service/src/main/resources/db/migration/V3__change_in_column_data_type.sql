ALTER TABLE payments
DROP
COLUMN order_id;

ALTER TABLE payments
    ADD order_id BIGINT NULL;
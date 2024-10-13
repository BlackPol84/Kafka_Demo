BEGIN;

ALTER TABLE account
    ADD COLUMN account_type VARCHAR(255);

ALTER TABLE account
    ADD COLUMN blocking BOOLEAN;

ALTER TABLE account
    ADD COLUMN credit_limit DECIMAL(19, 2);

COMMIT;
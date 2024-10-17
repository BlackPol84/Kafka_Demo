BEGIN;

ALTER TABLE account
    ADD COLUMN account_type VARCHAR(255);

ALTER TABLE account
    ADD COLUMN blocking BOOLEAN;

COMMIT;
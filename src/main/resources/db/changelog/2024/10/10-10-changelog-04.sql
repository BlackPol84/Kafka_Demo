BEGIN ;

ALTER TABLE transaction
    ADD COLUMN transaction_type VARCHAR(255);

ALTER TABLE transaction
    ADD COLUMN processed BOOLEAN;

COMMIT;

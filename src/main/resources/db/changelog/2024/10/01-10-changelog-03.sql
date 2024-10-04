BEGIN;

ALTER TABLE transaction
    ADD COLUMN account_id BIGINT;

ALTER TABLE transaction
    ADD CONSTRAINT fk_client
        FOREIGN KEY (client_id) REFERENCES client(id);

ALTER TABLE transaction
    ADD CONSTRAINT fk_account
        FOREIGN KEY (account_id) REFERENCES account(id);

COMMIT;
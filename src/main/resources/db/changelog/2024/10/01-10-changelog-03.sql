CREATE TABLE transaction (
                             id BIGSERIAL NOT NULL,
                             amount NUMERIC(19, 2),
                             client_id BIGINT,
                             account_id BIGINT,
                             CONSTRAINT pk_transaction PRIMARY KEY (id),
                             CONSTRAINT fk_transaction_client FOREIGN KEY (client_id) REFERENCES client(id),
                             CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES account(id)
);
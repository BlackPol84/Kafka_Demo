CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE account (
                         id BIGINT NOT NULL,
                         client_id BIGINT,
                         account_type VARCHAR(255),
                         balance DOUBLE PRECISION,
                         CONSTRAINT pk_account PRIMARY KEY (id),
                         CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);
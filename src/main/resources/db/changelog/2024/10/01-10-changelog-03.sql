CREATE TABLE account (
                         id BIGINT NOT NULL,
                         client_id BIGINT,
                         account_type VARCHAR(255),
                         balance DOUBLE PRECISION,
                         CONSTRAINT pk_account PRIMARY KEY (id),
                         CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);
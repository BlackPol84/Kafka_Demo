CREATE TABLE account (
                         id BIGSERIAL NOT NULL,
                         client_id BIGINT,
                         balance DOUBLE PRECISION,
                         CONSTRAINT pk_account PRIMARY KEY (id),
                         CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);
CREATE TABLE client (
                        id BIGSERIAL NOT NULL,
                        first_name VARCHAR(255),
                        last_name VARCHAR(255),
                        middle_name VARCHAR(255),
                        CONSTRAINT pk_client PRIMARY KEY (id)
);


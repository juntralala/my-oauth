CREATE TABLE auth_clients_grant_types (
    auth_client_id VARCHAR(100) NOT NULL,
    grant_type_id VARCHAR(100) NOT NULL,
    created_at DATETIME,
    PRIMARY KEY (auth_client_id, grant_type_id),
    FOREIGN KEY (auth_client_id) REFERENCES auth_clients(id),
    FOREIGN KEY (grant_type_id) REFERENCES grant_types(id)
);
CREATE TABLE auth_clients (
    id VARCHAR(100) PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    redirect_uri VARCHAR(2048) NOT NULL,
    default_scopes VARCHAR(100),
    client_secret VARCHAR(255),
    is_confidential BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NOT NULL,
    INDEX idx_client_secret (client_secret)
);
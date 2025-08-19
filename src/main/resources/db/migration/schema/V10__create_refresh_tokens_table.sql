CREATE TABLE refresh_tokens (
    id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    auth_client_id VARCHAR(100) NOT NULL,
    token VARCHAR(100) NOT NULL UNIQUE KEY,
    last_used_at DATETIME,
    created_at DATETIME NOT NULL,
    revoked_at DATETIME,

    FOREIGN KEY (user_id) REFERENCES users(id)
);
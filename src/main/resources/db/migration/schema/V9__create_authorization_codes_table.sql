CREATE TABLE authorization_codes (
    id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    auth_client_id VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    scopes VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME,
    code_challenge VARCHAR(255),
    code_challenge_method VARCHAR(10),
    UNIQUE KEY unique_code (code),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (auth_client_id) REFERENCES auth_clients(id)
);
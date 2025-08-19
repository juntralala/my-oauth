CREATE TABLE users_permissions (
    user_id VARCHAR(100) NOT NULL,
    permission_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, permission_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);
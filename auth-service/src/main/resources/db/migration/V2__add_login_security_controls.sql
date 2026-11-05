ALTER TABLE usuarios
    ADD COLUMN failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN locked_until TIMESTAMP WITH TIME ZONE;

CREATE TABLE auth_login_rate_limits (
    client_ip VARCHAR(45) PRIMARY KEY,
    window_start TIMESTAMP WITH TIME ZONE NOT NULL,
    request_count INTEGER NOT NULL
);

CREATE TABLE experience_result (
    session_id VARCHAR(36) NOT NULL,
    is_success BOOLEAN NULL,
    elapsed_seconds INT NOT NULL,
    error_count INT NULL,
    back_count INT NOT NULL DEFAULT 0,
    ai_help_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (session_id),
    CONSTRAINT fk_experience_result_session FOREIGN KEY (session_id) REFERENCES experience_session (session_id),
    CONSTRAINT chk_experience_result_elapsed CHECK (elapsed_seconds >= 0),
    CONSTRAINT chk_experience_result_error_count CHECK (error_count IS NULL OR error_count >= 0),
    CONSTRAINT chk_experience_result_back_count CHECK (back_count >= 0),
    CONSTRAINT chk_experience_result_ai_help_count CHECK (ai_help_count >= 0)
);

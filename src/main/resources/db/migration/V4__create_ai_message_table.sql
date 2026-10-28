CREATE TABLE ai_message (
    message_id BIGINT NOT NULL AUTO_INCREMENT,
    session_id VARCHAR(36) NOT NULL,
    step_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    response_time_ms INT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (message_id),
    INDEX idx_ai_message_session_step (session_id, step_id),
    CONSTRAINT fk_ai_message_session FOREIGN KEY (session_id) REFERENCES experience_session (session_id),
    CONSTRAINT fk_ai_message_step FOREIGN KEY (step_id) REFERENCES scenario_step (step_id),
    CONSTRAINT chk_ai_message_response_time CHECK (response_time_ms IS NULL OR response_time_ms >= 0)
);

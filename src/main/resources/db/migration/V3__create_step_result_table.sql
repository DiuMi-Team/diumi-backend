CREATE TABLE step_result (
    step_result_id BIGINT NOT NULL AUTO_INCREMENT,
    session_id VARCHAR(36) NOT NULL,
    step_id BIGINT NOT NULL,
    elapsed_ms INT NOT NULL,
    error_count INT NOT NULL DEFAULT 0,
    ai_help_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (step_result_id),
    CONSTRAINT fk_step_result_session FOREIGN KEY (session_id) REFERENCES experience_session (session_id),
    CONSTRAINT fk_step_result_step FOREIGN KEY (step_id) REFERENCES scenario_step (step_id),
    CONSTRAINT uk_step_result_session_step UNIQUE (session_id, step_id),
    CONSTRAINT chk_step_result_elapsed CHECK (elapsed_ms >= 0),
    CONSTRAINT chk_step_result_error_count CHECK (error_count >= 0),
    CONSTRAINT chk_step_result_ai_help_count CHECK (ai_help_count >= 0)
);

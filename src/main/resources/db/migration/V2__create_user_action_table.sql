CREATE TABLE user_action (
    action_id BIGINT NOT NULL AUTO_INCREMENT,
    session_id VARCHAR(36) NOT NULL,
    step_id BIGINT NOT NULL,
    action_type VARCHAR(30) NOT NULL,
    target_code VARCHAR(50) NULL,
    is_valid BOOLEAN NULL,
    action_data JSON NULL,
    occurred_at DATETIME NOT NULL,
    PRIMARY KEY (action_id),
    INDEX idx_user_action_session_step (session_id, step_id),
    CONSTRAINT fk_user_action_session FOREIGN KEY (session_id) REFERENCES experience_session (session_id),
    CONSTRAINT fk_user_action_step FOREIGN KEY (step_id) REFERENCES scenario_step (step_id)
);

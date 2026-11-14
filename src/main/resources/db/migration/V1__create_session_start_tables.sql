CREATE TABLE anonymous_visitor (
    visitor_id VARCHAR(36) NOT NULL,
    created_at DATETIME NOT NULL,
    last_accessed_at DATETIME NOT NULL,
    PRIMARY KEY (visitor_id)
);

CREATE TABLE scenario (
    scenario_id BIGINT NOT NULL AUTO_INCREMENT,
    scenario_type VARCHAR(20) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(300) NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (scenario_id)
);

CREATE TABLE mission (
    mission_id BIGINT NOT NULL AUTO_INCREMENT,
    scenario_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    target_data JSON NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimated_minutes INT NULL,
    PRIMARY KEY (mission_id),
    CONSTRAINT fk_mission_scenario FOREIGN KEY (scenario_id) REFERENCES scenario (scenario_id)
);

CREATE TABLE scenario_step (
    step_id BIGINT NOT NULL AUTO_INCREMENT,
    scenario_id BIGINT NOT NULL,
    step_code VARCHAR(50) NOT NULL,
    step_order INT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    guide_message VARCHAR(500) NULL,
    fallback_message VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (step_id),
    CONSTRAINT fk_scenario_step_scenario FOREIGN KEY (scenario_id) REFERENCES scenario (scenario_id),
    CONSTRAINT uk_scenario_step_code UNIQUE (scenario_id, step_code),
    CONSTRAINT uk_scenario_step_order UNIQUE (scenario_id, step_order)
);

CREATE TABLE mission_step_guide (
    guide_id BIGINT NOT NULL AUTO_INCREMENT,
    mission_id BIGINT NOT NULL,
    step_id BIGINT NOT NULL,
    guide_message VARCHAR(500) NOT NULL,
    highlight_target_code VARCHAR(50) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (guide_id),
    CONSTRAINT fk_guide_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id),
    CONSTRAINT fk_guide_step FOREIGN KEY (step_id) REFERENCES scenario_step (step_id),
    CONSTRAINT uk_mission_step_guide UNIQUE (mission_id, step_id)
);

CREATE TABLE experience_session (
    session_id VARCHAR(36) NOT NULL,
    scenario_id BIGINT NOT NULL,
    mission_id BIGINT NULL,
    visitor_id VARCHAR(36) NOT NULL,
    session_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    started_at DATETIME NOT NULL,
    ended_at DATETIME NULL,
    PRIMARY KEY (session_id),
    CONSTRAINT fk_session_scenario FOREIGN KEY (scenario_id) REFERENCES scenario (scenario_id),
    CONSTRAINT fk_session_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id),
    CONSTRAINT fk_session_visitor FOREIGN KEY (visitor_id) REFERENCES anonymous_visitor (visitor_id),
    CONSTRAINT chk_session_mission CHECK (
        (session_type = 'REAL_FREE' AND mission_id IS NULL)
        OR (session_type IN ('PRACTICE_MISSION', 'REAL_MISSION') AND mission_id IS NOT NULL)
    )
);

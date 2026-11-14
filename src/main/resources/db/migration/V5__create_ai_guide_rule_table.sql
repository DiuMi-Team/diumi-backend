CREATE TABLE ai_guide_rule (
    rule_id BIGINT NOT NULL AUTO_INCREMENT,
    scenario_type VARCHAR(20) NOT NULL,
    screen_code VARCHAR(50) NOT NULL,
    intent_code VARCHAR(50) NOT NULL,
    keywords TEXT NOT NULL,
    example_phrases TEXT NOT NULL,
    response_template VARCHAR(1000) NOT NULL,
    priority INT NOT NULL DEFAULT 100,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (rule_id),
    CONSTRAINT uk_ai_guide_rule_scope_intent
        UNIQUE (scenario_type, screen_code, intent_code)
);

CREATE INDEX idx_ai_guide_rule_lookup
    ON ai_guide_rule (scenario_type, screen_code, is_active, priority);

-- AI가 실제 키오스크 화면 구성을 알고 안내하도록 화면 정보와 버튼 목록을 저장한다.
-- ai_screen_context : 화면 하나(screen_code)의 이름과 설명
-- ai_screen_button  : 그 화면에 있는 버튼. button_code는 행동 기록의 target_code와 같은 값이다.

CREATE TABLE ai_screen_context (
    screen_id BIGINT NOT NULL AUTO_INCREMENT,
    scenario_type VARCHAR(20) NOT NULL,
    screen_code VARCHAR(50) NOT NULL,
    screen_name VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (screen_id),
    CONSTRAINT uk_ai_screen_context_scope UNIQUE (scenario_type, screen_code)
);

CREATE TABLE ai_screen_button (
    button_id BIGINT NOT NULL AUTO_INCREMENT,
    screen_id BIGINT NOT NULL,
    button_code VARCHAR(50) NULL,
    label VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    display_order INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (button_id),
    CONSTRAINT fk_ai_screen_button_screen FOREIGN KEY (screen_id) REFERENCES ai_screen_context (screen_id)
);

CREATE INDEX idx_ai_screen_button_screen
    ON ai_screen_button (screen_id, display_order);

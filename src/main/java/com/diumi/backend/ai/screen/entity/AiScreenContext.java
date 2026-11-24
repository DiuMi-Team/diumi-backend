package com.diumi.backend.ai.screen.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 키오스크 화면 하나(screen_code)의 이름과 설명.
 * AI가 실제 화면 구성을 알고 안내하도록 프롬프트에 넣는다.
 */
@Entity
@Table(name = "ai_screen_context", uniqueConstraints =
        @UniqueConstraint(name = "uk_ai_screen_context_scope",
                columnNames = {"scenario_type", "screen_code"}))
public class AiScreenContext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;

    @Column(name = "scenario_type", nullable = false, length = 20)
    private String scenarioType;

    @Column(name = "screen_code", nullable = false, length = 50)
    private String screenCode;

    @Column(name = "screen_name", nullable = false, length = 100)
    private String screenName;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected AiScreenContext() {
    }

    public AiScreenContext(String scenarioType, String screenCode, String screenName,
                           String description, boolean active) {
        this.scenarioType = scenarioType;
        this.screenCode = screenCode;
        this.screenName = screenName;
        this.description = description;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getScenarioType() { return scenarioType; }
    public String getScreenCode() { return screenCode; }
    public String getScreenName() { return screenName; }
    public String getDescription() { return description; }
}

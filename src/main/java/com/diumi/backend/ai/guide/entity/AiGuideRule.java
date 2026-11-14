package com.diumi.backend.ai.guide.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "ai_guide_rule", uniqueConstraints =
        @UniqueConstraint(name = "uk_ai_guide_rule_scope_intent",
                columnNames = {"scenario_type", "screen_code", "intent_code"}))
public class AiGuideRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long id;

    @Column(name = "scenario_type", nullable = false, length = 20)
    private String scenarioType;

    @Column(name = "screen_code", nullable = false, length = 50)
    private String screenCode;

    @Column(name = "intent_code", nullable = false, length = 50)
    private String intentCode;

    @Column(nullable = false, columnDefinition = "text")
    private String keywords;

    @Column(name = "example_phrases", nullable = false, columnDefinition = "text")
    private String examplePhrases;

    @Column(name = "response_template", nullable = false, length = 1000)
    private String responseTemplate;

    @Column(nullable = false)
    private int priority;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected AiGuideRule() {
    }

    public AiGuideRule(String scenarioType, String screenCode, String intentCode,
                       String keywords, String examplePhrases, String responseTemplate,
                       int priority, boolean active) {
        this.scenarioType = scenarioType;
        this.screenCode = screenCode;
        this.intentCode = intentCode;
        this.keywords = keywords;
        this.examplePhrases = examplePhrases;
        this.responseTemplate = responseTemplate;
        this.priority = priority;
        this.active = active;
    }

    public String getScreenCode() { return screenCode; }
    public String getIntentCode() { return intentCode; }
    public String getKeywords() { return keywords; }
    public String getExamplePhrases() { return examplePhrases; }
    public String getResponseTemplate() { return responseTemplate; }
}

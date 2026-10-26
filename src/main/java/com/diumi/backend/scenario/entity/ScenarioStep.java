package com.diumi.backend.scenario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "scenario_step", uniqueConstraints = {
        @UniqueConstraint(name = "uk_scenario_step_code", columnNames = {"scenario_id", "step_code"}),
        @UniqueConstraint(name = "uk_scenario_step_order", columnNames = {"scenario_id", "step_order"})
})
public class ScenarioStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @Column(name = "step_code", nullable = false, length = 50)
    private String stepCode;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;

    @Column(name = "guide_message", length = 500)
    private String guideMessage;

    @Column(name = "fallback_message", length = 500)
    private String fallbackMessage;

    protected ScenarioStep() {
    }

    public ScenarioStep(Scenario scenario, String stepCode, int stepOrder, String stepName, String guideMessage) {
        this.scenario = scenario;
        this.stepCode = stepCode;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.guideMessage = guideMessage;
    }

    public Long getId() { return id; }
    public int getStepOrder() { return stepOrder; }
    public String getStepName() { return stepName; }
    public String getGuideMessage() { return guideMessage; }
}

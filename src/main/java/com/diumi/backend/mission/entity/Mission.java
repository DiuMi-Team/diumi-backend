package com.diumi.backend.mission.entity;

import com.diumi.backend.scenario.entity.Scenario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mission")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(name = "target_data", nullable = false, columnDefinition = "json")
    private String targetData;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected Mission() {
    }

    public Mission(Scenario scenario, String title, String description, String difficulty, String targetData, boolean active) {
        this.scenario = scenario;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.targetData = targetData;
        this.active = active;
    }

    public Long getId() { return id; }
    public Long getScenarioId() { return scenario.getId(); }
    public String getTargetData() { return targetData; }
}

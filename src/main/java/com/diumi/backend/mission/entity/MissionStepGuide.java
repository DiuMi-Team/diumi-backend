package com.diumi.backend.mission.entity;

import com.diumi.backend.scenario.entity.ScenarioStep;
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
@Table(name = "mission_step_guide", uniqueConstraints =
        @UniqueConstraint(name = "uk_mission_step_guide", columnNames = {"mission_id", "step_id"}))
public class MissionStepGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private ScenarioStep step;

    @Column(name = "guide_message", nullable = false, length = 500)
    private String guideMessage;

    @Column(name = "highlight_target_code", length = 50)
    private String highlightTargetCode;

    protected MissionStepGuide() {
    }

    public MissionStepGuide(Mission mission, ScenarioStep step, String guideMessage, String highlightTargetCode) {
        this.mission = mission;
        this.step = step;
        this.guideMessage = guideMessage;
        this.highlightTargetCode = highlightTargetCode;
    }

    public String getGuideMessage() { return guideMessage; }
    public String getHighlightTargetCode() { return highlightTargetCode; }
}

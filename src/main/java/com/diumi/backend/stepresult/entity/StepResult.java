package com.diumi.backend.stepresult.entity;

import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.session.entity.ExperienceSession;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "step_result", uniqueConstraints =
        @UniqueConstraint(name = "uk_step_result_session_step", columnNames = {"session_id", "step_id"}))
public class StepResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ExperienceSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private ScenarioStep step;

    @Column(name = "elapsed_ms", nullable = false)
    private int elapsedMs;

    @Column(name = "error_count", nullable = false)
    private int errorCount;

    @Column(name = "ai_help_count", nullable = false)
    private int aiHelpCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected StepResult() {
    }

    public StepResult(ExperienceSession session, ScenarioStep step, int elapsedMs,
                      int errorCount, int aiHelpCount, LocalDateTime createdAt) {
        this.session = session;
        this.step = step;
        this.elapsedMs = elapsedMs;
        this.errorCount = errorCount;
        this.aiHelpCount = aiHelpCount;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
}

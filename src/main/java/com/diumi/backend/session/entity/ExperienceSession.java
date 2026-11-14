package com.diumi.backend.session.entity;

import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "experience_session")
public class ExperienceSession {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "session_id", columnDefinition = "varchar(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visitor_id", nullable = false)
    private AnonymousVisitor visitor;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 30)
    private SessionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    protected ExperienceSession() {
    }

    public ExperienceSession(UUID id, Scenario scenario, Mission mission, AnonymousVisitor visitor,
                             SessionType type, LocalDateTime startedAt) {
        this.id = id;
        this.scenario = scenario;
        this.mission = mission;
        this.visitor = visitor;
        this.type = type;
        this.status = SessionStatus.IN_PROGRESS;
        this.startedAt = startedAt;
    }

    public UUID getId() { return id; }
    public Long getScenarioId() { return scenario.getId(); }
    public Mission getMission() { return mission; }
    public UUID getVisitorId() { return visitor.getId(); }
    public SessionType getType() { return type; }
    public SessionStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
}

package com.diumi.backend.ai.entity;

import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.session.entity.ExperienceSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_message")
public class AiMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ExperienceSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private ScenarioStep step;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AiMessageRole role;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AiMessage() {
    }

    public AiMessage(ExperienceSession session, ScenarioStep step, AiMessageRole role,
                     String content, Integer responseTimeMs, LocalDateTime createdAt) {
        this.session = session;
        this.step = step;
        this.role = role;
        this.content = content;
        this.responseTimeMs = responseTimeMs;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public AiMessageRole getRole() { return role; }
    public String getContent() { return content; }
    public Integer getResponseTimeMs() { return responseTimeMs; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

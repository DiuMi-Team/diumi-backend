package com.diumi.backend.result.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "experience_result")
public class ExperienceResult {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "session_id", columnDefinition = "varchar(36)")
    private UUID sessionId;

    @Column(name = "is_success")
    private Boolean success;

    @Column(name = "elapsed_seconds", nullable = false)
    private int elapsedSeconds;

    @Column(name = "error_count")
    private Integer errorCount;

    @Column(name = "back_count", nullable = false)
    private int backCount;

    @Column(name = "ai_help_count", nullable = false)
    private int aiHelpCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ExperienceResult() {
    }

    public ExperienceResult(UUID sessionId, Boolean success, int elapsedSeconds, Integer errorCount,
                            int backCount, int aiHelpCount, LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.success = success;
        this.elapsedSeconds = elapsedSeconds;
        this.errorCount = errorCount;
        this.backCount = backCount;
        this.aiHelpCount = aiHelpCount;
        this.createdAt = createdAt;
    }

    public UUID getSessionId() { return sessionId; }
    public Boolean getSuccess() { return success; }
    public int getElapsedSeconds() { return elapsedSeconds; }
    public Integer getErrorCount() { return errorCount; }
    public int getBackCount() { return backCount; }
    public int getAiHelpCount() { return aiHelpCount; }
}

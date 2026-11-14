package com.diumi.backend.action.entity;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "user_action")
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ExperienceSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private ScenarioStep step;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private ActionType actionType;

    @Column(name = "target_code", length = 50)
    private String targetCode;

    @Column(name = "is_valid")
    private Boolean valid;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "action_data", columnDefinition = "json")
    private JsonNode actionData;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    protected UserAction() {
    }

    public UserAction(ExperienceSession session, ScenarioStep step, ActionType actionType,
                      String targetCode, Boolean valid, JsonNode actionData, LocalDateTime occurredAt) {
        this.session = session;
        this.step = step;
        this.actionType = actionType;
        this.targetCode = targetCode;
        this.valid = valid;
        this.actionData = actionData;
        this.occurredAt = occurredAt;
    }

    public Long getId() { return id; }
    public Boolean getValid() { return valid; }
    public ActionType getActionType() { return actionType; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}

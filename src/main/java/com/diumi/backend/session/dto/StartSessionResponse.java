package com.diumi.backend.session.dto;

import com.diumi.backend.session.entity.SessionStatus;
import com.diumi.backend.session.entity.SessionType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record StartSessionResponse(
        UUID sessionId,
        Long scenarioId,
        Long missionId,
        SessionType sessionType,
        SessionStatus status,
        OffsetDateTime startedAt,
        long totalSteps,
        boolean aiHelpEnabled,
        CurrentStep currentStep
) {
    public record CurrentStep(
            Long stepId,
            int stepOrder,
            String stepName,
            String guideMessage,
            String highlightTargetCode
    ) {
    }
}

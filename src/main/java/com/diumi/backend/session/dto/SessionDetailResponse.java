package com.diumi.backend.session.dto;

import com.diumi.backend.session.entity.SessionStatus;
import com.diumi.backend.session.entity.SessionType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SessionDetailResponse(
        UUID sessionId,
        Long scenarioId,
        Long missionId,
        SessionType sessionType,
        SessionStatus status,
        OffsetDateTime startedAt,
        OffsetDateTime endedAt,
        List<Long> completedStepIds,
        int totalSteps,
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

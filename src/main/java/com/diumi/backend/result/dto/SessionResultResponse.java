package com.diumi.backend.result.dto;

import com.diumi.backend.session.entity.SessionType;
import java.util.List;
import java.util.UUID;

public record SessionResultResponse(
        UUID sessionId,
        SessionType sessionType,
        String scenarioTitle,
        String missionTitle,
        Boolean isSuccess,
        int elapsedSeconds,
        int aiHelpCount,
        Integer errorCount,
        int backCount,
        List<WrongStep> wrongSteps
) {
    public record WrongStep(
            Long stepId,
            String stepName,
            int errorCount
    ) {
    }
}

package com.diumi.backend.scenario.dto;

import java.util.List;

public record ScenarioDetailResponse(
        Long scenarioId,
        String scenarioType,
        String title,
        List<Step> steps
) {
    public record Step(
            Long stepId,
            String stepCode,
            int stepOrder,
            String stepName
    ) {
    }
}

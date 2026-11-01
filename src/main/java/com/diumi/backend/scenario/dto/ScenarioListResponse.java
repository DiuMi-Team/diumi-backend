package com.diumi.backend.scenario.dto;

import java.util.List;

public record ScenarioListResponse(
        List<ScenarioSummary> scenarios
) {
    public record ScenarioSummary(
            Long scenarioId,
            String scenarioType,
            String title,
            String description,
            String thumbnailUrl
    ) {
    }
}

package com.diumi.backend.mission.dto;

import java.util.List;

public record MissionListResponse(
        List<MissionSummary> missions
) {
    public record MissionSummary(
            Long missionId,
            String title,
            String description,
            String difficulty,
            Integer estimatedMinutes
    ) {
    }
}

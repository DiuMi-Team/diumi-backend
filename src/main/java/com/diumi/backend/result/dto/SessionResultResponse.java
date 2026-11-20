package com.diumi.backend.result.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
        List<WrongStep> wrongSteps,
        @Schema(description = "미션 완료 조건별 결과. 자유 체험이거나 조건이 없는 미션이면 빈 배열")
        List<ConditionResult> conditions,
        @Schema(description = "완료 조건 중 맞힌 비율(0~100). 조건이 없으면 null", example = "75")
        Integer conditionPassRate
) {
    public record WrongStep(
            Long stepId,
            String stepName,
            int errorCount
    ) {
    }

    public record ConditionResult(
            @Schema(description = "조건 코드", example = "SIZE_QUANTITY")
            String code,
            @Schema(description = "조건 이름", example = "Large 사이즈 · 수량 2잔")
            String condition,
            @Schema(description = "조건을 맞혔는지")
            boolean passed,
            @Schema(description = "틀렸을 때 다른 점. 맞혔으면 null", example = "Regular를 골랐어요")
            String detail
    ) {
    }
}

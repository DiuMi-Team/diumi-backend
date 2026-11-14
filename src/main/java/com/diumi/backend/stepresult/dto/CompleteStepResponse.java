package com.diumi.backend.stepresult.dto;

public record CompleteStepResponse(
        Long stepResultId,
        Long stepId,
        int elapsedMs,
        int errorCount,
        int aiHelpCount,
        NextStep nextStep,
        boolean isLastStep
) {
    public record NextStep(
            Long stepId,
            int stepOrder,
            String stepName,
            String guideMessage,
            String highlightTargetCode
    ) {
    }
}

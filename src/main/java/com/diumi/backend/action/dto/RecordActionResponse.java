package com.diumi.backend.action.dto;

public record RecordActionResponse(
        Long actionId,
        Boolean isValid,
        String feedbackMessage,
        String highlightTargetCode
) {
}

package com.diumi.backend.ai.dto;

public record SaveAiMessagesResponse(
        Long questionMessageId,
        Long answerMessageId
) {
}

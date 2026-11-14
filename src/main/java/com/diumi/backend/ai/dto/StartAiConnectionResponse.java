package com.diumi.backend.ai.dto;

import java.time.OffsetDateTime;

public record StartAiConnectionResponse(
        String clientSecret,
        OffsetDateTime expiresAt,
        String model,
        String conversationId
) {
}

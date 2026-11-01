package com.diumi.backend.result.dto;

import com.diumi.backend.session.entity.SessionStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EndSessionResponse(
        UUID sessionId,
        SessionStatus status,
        OffsetDateTime endedAt,
        boolean resultAvailable
) {
}

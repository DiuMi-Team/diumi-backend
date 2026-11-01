package com.diumi.backend.visitor.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegisterVisitorResponse(
        UUID visitorId,
        boolean isNew,
        OffsetDateTime createdAt,
        OffsetDateTime lastAccessedAt
) {
}

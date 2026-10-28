package com.diumi.backend.stepresult.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CompleteStepRequest(
        @NotNull @PositiveOrZero Integer elapsedMs
) {
}

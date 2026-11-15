package com.diumi.backend.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "AI 질문·답변 기록 저장 요청")
public record SaveAiMessagesRequest(
        @Schema(description = "질문이 발생한 단계 ID", example = "14")
        @NotNull @Positive Long stepId,
        @Schema(description = "사용자 질문의 최종 transcript", example = "카드는 어디에 넣어요?")
        @NotBlank @Size(max = 4000) String question,
        @Schema(description = "AI 답변의 최종 transcript", example = "카드를 카드 투입구에 넣어 주세요.")
        @NotBlank @Size(max = 4000) String answer,
        @Schema(description = "사용자 발화 종료부터 AI 답변 시작까지의 시간(ms)", example = "1840")
        @PositiveOrZero Integer responseTimeMs
) {
}

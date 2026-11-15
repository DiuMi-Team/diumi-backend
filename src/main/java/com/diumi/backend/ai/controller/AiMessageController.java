package com.diumi.backend.ai.controller;

import com.diumi.backend.ai.dto.SaveAiMessagesRequest;
import com.diumi.backend.ai.dto.SaveAiMessagesResponse;
import com.diumi.backend.ai.service.AiMessageService;
import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions/{sessionId}/ai/messages")
@Tag(name = "7. AI")
public class AiMessageController {
    private final AiMessageService messageService;

    public AiMessageController(AiMessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @Operation(summary = "AI 대화 기록 저장", description = "브라우저가 OpenAI Realtime에서 받은 최종 사용자 질문과 AI 답변 transcript를 하나의 트랜잭션으로 저장합니다. 마지막 질문 후 세션이 종료되어도 저장할 수 있습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "질문·답변 저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "필수값 누락 또는 길이 초과(INVALID_REQUEST)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "다른 방문자의 세션 또는 연습 세션(FORBIDDEN_SESSION, AI_NOT_ALLOWED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션 또는 단계 없음(SESSION_NOT_FOUND, STEP_NOT_FOUND)")
    })
    public ResponseEntity<ApiResponse<SaveAiMessagesResponse>> save(
            @Parameter(description = "세션 UUID") @PathVariable UUID sessionId,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody SaveAiMessagesRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(messageService.save(sessionId, visitor, request)));
    }
}

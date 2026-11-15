package com.diumi.backend.ai.controller;

import com.diumi.backend.ai.dto.StartAiConnectionRequest;
import com.diumi.backend.ai.dto.StartAiConnectionResponse;
import com.diumi.backend.ai.service.AiConnectionService;
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
@RequestMapping("/sessions/{sessionId}/ai/connections")
@Tag(name = "7. AI")
public class AiConnectionController {
    private final AiConnectionService connectionService;

    public AiConnectionController(AiConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping
    @Operation(summary = "AI 음성 연결 시작", description = "현재 미션·단계·화면별 안내 규칙이 포함된 OpenAI Realtime 세션을 만들고 브라우저가 WebRTC 연결에 사용할 단기 임시 키를 반환합니다. 실전 세션에서만 사용할 수 있습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "임시 키 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 형식 오류(INVALID_REQUEST)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "다른 방문자의 세션 또는 연습 세션(FORBIDDEN_SESSION, AI_NOT_ALLOWED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션 또는 단계 없음(SESSION_NOT_FOUND, STEP_NOT_FOUND)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 종료된 세션(SESSION_ALREADY_ENDED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "OpenAI 연결 실패(AI_CONNECTION_FAILED)")
    })
    public ResponseEntity<ApiResponse<StartAiConnectionResponse>> start(
            @Parameter(description = "세션 UUID") @PathVariable UUID sessionId,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody StartAiConnectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(connectionService.start(sessionId, visitor, request)));
    }
}

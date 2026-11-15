package com.diumi.backend.session.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.session.dto.StartSessionRequest;
import com.diumi.backend.session.dto.StartSessionResponse;
import com.diumi.backend.session.dto.SessionDetailResponse;
import com.diumi.backend.session.service.ExperienceSessionService;
import com.diumi.backend.session.service.SessionQueryService;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
@Tag(name = "4. 체험 세션")
public class ExperienceSessionController {
    private final ExperienceSessionService sessionService;
    private final SessionQueryService sessionQueryService;

    public ExperienceSessionController(ExperienceSessionService sessionService,
                                       SessionQueryService sessionQueryService) {
        this.sessionService = sessionService;
        this.sessionQueryService = sessionQueryService;
    }

    @PostMapping
    @Operation(summary = "체험 세션 시작", description = "선택한 시나리오와 세션 유형으로 진행 중 세션을 만들고 첫 단계 정보를 반환합니다. REAL_FREE는 mission_id를 보내지 않습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "세션 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 오류 또는 미션-시나리오 불일치(INVALID_REQUEST, MISSION_SCENARIO_MISMATCH)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "시나리오 또는 미션 없음(SCENARIO_NOT_FOUND, MISSION_NOT_FOUND)")
    })
    public ResponseEntity<ApiResponse<StartSessionResponse>> start(
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody StartSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(sessionService.start(visitor, request)));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "체험 세션 조회", description = "세션 상태, 완료 단계 목록, 현재 진행할 단계를 조회합니다. 종료된 세션의 current_step은 null입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "다른 방문자의 세션(FORBIDDEN_SESSION)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션 없음(SESSION_NOT_FOUND)")
    })
    public ResponseEntity<ApiResponse<SessionDetailResponse>> get(
            @Parameter(description = "세션 UUID") @PathVariable UUID sessionId,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(sessionQueryService.get(sessionId, visitor)));
    }
}

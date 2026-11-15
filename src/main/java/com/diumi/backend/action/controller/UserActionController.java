package com.diumi.backend.action.controller;

import com.diumi.backend.action.dto.RecordActionRequest;
import com.diumi.backend.action.dto.RecordActionResponse;
import com.diumi.backend.action.service.UserActionService;
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
@RequestMapping("/sessions/{sessionId}/actions")
@Tag(name = "5. 행동")
public class UserActionController {
    private final UserActionService actionService;

    public UserActionController(UserActionService actionService) {
        this.actionService = actionService;
    }

    @PostMapping
    @Operation(summary = "사용자 행동 기록", description = "실행·결제 화면에서 발생한 선택, 옵션, 수량 변경, 이동 등의 행동을 저장합니다. 실전 미션은 목표 데이터와 비교한 정답 여부를 함께 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "행동 기록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "필수값 누락 또는 형식 오류(INVALID_REQUEST)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "다른 방문자의 세션(FORBIDDEN_SESSION)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션 또는 단계 없음(SESSION_NOT_FOUND, STEP_NOT_FOUND)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 종료된 세션(SESSION_ALREADY_ENDED)")
    })
    public ResponseEntity<ApiResponse<RecordActionResponse>> record(
            @Parameter(description = "세션 UUID") @PathVariable UUID sessionId,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody RecordActionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(actionService.record(sessionId, visitor, request)));
    }
}

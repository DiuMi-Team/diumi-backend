package com.diumi.backend.stepresult.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.stepresult.dto.CompleteStepRequest;
import com.diumi.backend.stepresult.dto.CompleteStepResponse;
import com.diumi.backend.stepresult.service.StepResultService;
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
@RequestMapping("/sessions/{sessionId}/steps/{stepId}/complete")
@Tag(name = "6. 단계")
public class StepResultController {
    private final StepResultService stepResultService;

    public StepResultController(StepResultService stepResultService) {
        this.stepResultService = stepResultService;
    }

    @PostMapping
    @Operation(summary = "단계 완료 기록", description = "현재 단계를 완료하고 소요 시간, 오답 횟수, AI 도움 횟수를 집계합니다. 마지막 단계가 아니면 다음 단계 정보를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "단계 완료 기록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 형식 오류(INVALID_REQUEST)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "다른 방문자의 세션(FORBIDDEN_SESSION)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션 또는 단계 없음(SESSION_NOT_FOUND, STEP_NOT_FOUND)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "종료된 세션 또는 중복 완료(SESSION_ALREADY_ENDED, STEP_ALREADY_COMPLETED)")
    })
    public ResponseEntity<ApiResponse<CompleteStepResponse>> complete(
            @Parameter(description = "세션 UUID") @PathVariable UUID sessionId,
            @Parameter(description = "완료할 단계 ID", example = "11") @PathVariable Long stepId,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody CompleteStepRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(stepResultService.complete(sessionId, stepId, visitor, request)));
    }
}

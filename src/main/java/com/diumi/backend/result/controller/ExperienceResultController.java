package com.diumi.backend.result.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.result.dto.EndSessionRequest;
import com.diumi.backend.result.dto.EndSessionResponse;
import com.diumi.backend.result.dto.SessionResultResponse;
import com.diumi.backend.result.service.ExperienceResultService;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions/{sessionId}")
@Tag(name = "8. 결과")
public class ExperienceResultController {
    private final ExperienceResultService resultService;

    public ExperienceResultController(ExperienceResultService resultService) {
        this.resultService = resultService;
    }

    @PostMapping("/end")
    @Operation(summary = "체험 세션 종료", description = "세션을 COMPLETED 또는 ABANDONED 상태로 종료합니다. 정상 완료 시 단계 결과와 행동 기록을 집계해 수행 결과를 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "세션 종료 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "종료 상태 형식 오류(INVALID_REQUEST)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "다른 방문자의 세션(FORBIDDEN_SESSION)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션 없음(SESSION_NOT_FOUND)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 종료됐거나 완료할 단계가 남음(SESSION_ALREADY_ENDED, SESSION_NOT_READY_TO_COMPLETE)")
    })
    public ResponseEntity<ApiResponse<EndSessionResponse>> end(
            @Parameter(description = "세션 UUID") @PathVariable UUID sessionId,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor,
            @Valid @RequestBody EndSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(resultService.end(sessionId, visitor, request)));
    }

    @GetMapping("/result")
    @Operation(summary = "수행 결과 조회", description = "정상 완료된 세션의 성공 여부, 총 소요 시간, 오답·뒤로가기·AI 도움 횟수와 오답 단계를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결과 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "다른 방문자의 세션(FORBIDDEN_SESSION)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션 또는 수행 결과 없음(SESSION_NOT_FOUND, RESULT_NOT_FOUND)")
    })
    public ResponseEntity<ApiResponse<SessionResultResponse>> result(
            @Parameter(description = "세션 UUID") @PathVariable UUID sessionId,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(resultService.getResult(sessionId, visitor)));
    }
}

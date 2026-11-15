package com.diumi.backend.scenario.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.scenario.dto.ScenarioDetailResponse;
import com.diumi.backend.scenario.dto.ScenarioListResponse;
import com.diumi.backend.scenario.service.ScenarioQueryService;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scenarios")
@Tag(name = "2. 시나리오")
public class ScenarioController {
    private final ScenarioQueryService scenarioQueryService;

    public ScenarioController(ScenarioQueryService scenarioQueryService) {
        this.scenarioQueryService = scenarioQueryService;
    }

    @GetMapping
    @Operation(summary = "시나리오 목록 조회", description = "현재 체험할 수 있는 활성 시나리오를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 헤더 누락 또는 미등록 방문자(VISITOR_REQUIRED)")
    })
    public ResponseEntity<ApiResponse<ScenarioListResponse>> list(
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(scenarioQueryService.getActiveScenarios()));
    }

    @GetMapping("/{scenarioId}")
    @Operation(summary = "시나리오 상세 조회", description = "시나리오 기본 정보와 순서대로 정렬된 전체 단계를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "시나리오 없음(SCENARIO_NOT_FOUND)")
    })
    public ResponseEntity<ApiResponse<ScenarioDetailResponse>> get(
            @Parameter(description = "시나리오 ID", example = "1") @PathVariable Long scenarioId,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(scenarioQueryService.getDetail(scenarioId)));
    }
}

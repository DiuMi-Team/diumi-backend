package com.diumi.backend.mission.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.mission.dto.MissionListResponse;
import com.diumi.backend.mission.entity.MissionDifficulty;
import com.diumi.backend.mission.service.MissionQueryService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scenarios/{scenarioId}/missions")
@Tag(name = "3. 미션")
public class MissionController {
    private final MissionQueryService missionQueryService;

    public MissionController(MissionQueryService missionQueryService) {
        this.missionQueryService = missionQueryService;
    }

    @GetMapping
    @Operation(summary = "미션 목록 조회", description = "시나리오의 활성 미션을 조회합니다. difficulty를 전달하면 난이도로 필터링합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "방문자 등록 필요(VISITOR_REQUIRED)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "시나리오 없음(SCENARIO_NOT_FOUND)")
    })
    public ResponseEntity<ApiResponse<MissionListResponse>> list(
            @Parameter(description = "시나리오 ID", example = "1") @PathVariable Long scenarioId,
            @Parameter(description = "난이도 필터", example = "EASY") @RequestParam(required = false) MissionDifficulty difficulty,
            @Parameter(hidden = true) @CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(missionQueryService.getMissions(scenarioId, difficulty)));
    }
}

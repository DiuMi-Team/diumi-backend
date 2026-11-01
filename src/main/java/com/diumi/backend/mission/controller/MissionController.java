package com.diumi.backend.mission.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.mission.dto.MissionListResponse;
import com.diumi.backend.mission.entity.MissionDifficulty;
import com.diumi.backend.mission.service.MissionQueryService;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scenarios/{scenarioId}/missions")
public class MissionController {
    private final MissionQueryService missionQueryService;

    public MissionController(MissionQueryService missionQueryService) {
        this.missionQueryService = missionQueryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MissionListResponse>> list(
            @PathVariable Long scenarioId,
            @RequestParam(required = false) MissionDifficulty difficulty,
            @CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(missionQueryService.getMissions(scenarioId, difficulty)));
    }
}

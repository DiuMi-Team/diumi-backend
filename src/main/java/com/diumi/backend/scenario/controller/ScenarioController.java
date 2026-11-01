package com.diumi.backend.scenario.controller;

import com.diumi.backend.common.api.ApiResponse;
import com.diumi.backend.scenario.dto.ScenarioListResponse;
import com.diumi.backend.scenario.service.ScenarioQueryService;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import com.diumi.backend.visitor.web.CurrentVisitor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scenarios")
public class ScenarioController {
    private final ScenarioQueryService scenarioQueryService;

    public ScenarioController(ScenarioQueryService scenarioQueryService) {
        this.scenarioQueryService = scenarioQueryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ScenarioListResponse>> list(@CurrentVisitor AnonymousVisitor visitor) {
        return ResponseEntity.ok(ApiResponse.success(scenarioQueryService.getActiveScenarios()));
    }
}

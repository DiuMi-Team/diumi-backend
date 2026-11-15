package com.diumi.backend.scenario.service;

import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.scenario.dto.ScenarioDetailResponse;
import com.diumi.backend.scenario.dto.ScenarioListResponse;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioRepository;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScenarioQueryService {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioStepRepository stepRepository;

    public ScenarioQueryService(ScenarioRepository scenarioRepository, ScenarioStepRepository stepRepository) {
        this.scenarioRepository = scenarioRepository;
        this.stepRepository = stepRepository;
    }

    @Transactional(readOnly = true)
    public ScenarioListResponse getActiveScenarios() {
        List<ScenarioListResponse.ScenarioSummary> scenarios = scenarioRepository
                .findAllByActiveTrueOrderByIdAsc().stream()
                .map(this::toSummary)
                .toList();
        return new ScenarioListResponse(scenarios);
    }

    @Transactional(readOnly = true)
    public ScenarioDetailResponse getDetail(Long scenarioId) {
        Scenario scenario = scenarioRepository.findByIdAndActiveTrue(scenarioId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCENARIO_NOT_FOUND));
        List<ScenarioDetailResponse.Step> steps = stepRepository
                .findAllByScenarioIdOrderByStepOrderAsc(scenario.getId()).stream()
                .map(this::toStep)
                .toList();
        return new ScenarioDetailResponse(scenario.getId(), scenario.getType(), scenario.getTitle(), steps);
    }

    private ScenarioDetailResponse.Step toStep(ScenarioStep step) {
        return new ScenarioDetailResponse.Step(step.getId(), step.getStepCode(), step.getStepOrder(),
                step.getStepName());
    }

    private ScenarioListResponse.ScenarioSummary toSummary(Scenario scenario) {
        return new ScenarioListResponse.ScenarioSummary(scenario.getId(), scenario.getType(),
                scenario.getTitle(), scenario.getDescription(), scenario.getThumbnailUrl());
    }
}

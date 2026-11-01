package com.diumi.backend.scenario.service;

import com.diumi.backend.scenario.dto.ScenarioListResponse;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.repository.ScenarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScenarioQueryService {
    private final ScenarioRepository scenarioRepository;

    public ScenarioQueryService(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Transactional(readOnly = true)
    public ScenarioListResponse getActiveScenarios() {
        List<ScenarioListResponse.ScenarioSummary> scenarios = scenarioRepository
                .findAllByActiveTrueOrderByIdAsc().stream()
                .map(this::toSummary)
                .toList();
        return new ScenarioListResponse(scenarios);
    }

    private ScenarioListResponse.ScenarioSummary toSummary(Scenario scenario) {
        return new ScenarioListResponse.ScenarioSummary(scenario.getId(), scenario.getType(),
                scenario.getTitle(), scenario.getDescription(), scenario.getThumbnailUrl());
    }
}

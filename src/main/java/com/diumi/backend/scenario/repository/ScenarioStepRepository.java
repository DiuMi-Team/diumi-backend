package com.diumi.backend.scenario.repository;

import com.diumi.backend.scenario.entity.ScenarioStep;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioStepRepository extends JpaRepository<ScenarioStep, Long> {
    long countByScenario_Id(Long scenarioId);
    Optional<ScenarioStep> findFirstByScenario_IdOrderByStepOrderAsc(Long scenarioId);
    Optional<ScenarioStep> findFirstByScenario_IdAndStepOrderGreaterThanOrderByStepOrderAsc(
            Long scenarioId, int stepOrder);
    List<ScenarioStep> findAllByScenario_IdOrderByStepOrderAsc(Long scenarioId);
}

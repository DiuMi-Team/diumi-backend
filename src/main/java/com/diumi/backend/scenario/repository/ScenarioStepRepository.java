package com.diumi.backend.scenario.repository;

import com.diumi.backend.scenario.entity.ScenarioStep;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioStepRepository extends JpaRepository<ScenarioStep, Long> {
    long countByScenarioId(Long scenarioId);
    Optional<ScenarioStep> findFirstByScenarioIdOrderByStepOrderAsc(Long scenarioId);
}

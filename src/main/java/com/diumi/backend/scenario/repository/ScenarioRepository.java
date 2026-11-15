package com.diumi.backend.scenario.repository;

import com.diumi.backend.scenario.entity.Scenario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findAllByActiveTrueOrderByIdAsc();
    Optional<Scenario> findByIdAndActiveTrue(Long id);
}

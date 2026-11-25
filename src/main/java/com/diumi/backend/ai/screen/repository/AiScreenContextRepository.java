package com.diumi.backend.ai.screen.repository;

import com.diumi.backend.ai.screen.entity.AiScreenContext;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiScreenContextRepository extends JpaRepository<AiScreenContext, Long> {
    Optional<AiScreenContext> findByScenarioTypeAndScreenCodeAndActiveTrue(
            String scenarioType, String screenCode);
}

package com.diumi.backend.ai.guide.repository;

import com.diumi.backend.ai.guide.entity.AiGuideRule;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiGuideRuleRepository extends JpaRepository<AiGuideRule, Long> {
    List<AiGuideRule> findByScenarioTypeAndScreenCodeInAndActiveTrueOrderByPriorityAscIdAsc(
            String scenarioType, Collection<String> screenCodes);
}

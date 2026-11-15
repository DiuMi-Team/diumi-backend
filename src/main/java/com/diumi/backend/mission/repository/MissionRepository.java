package com.diumi.backend.mission.repository;

import com.diumi.backend.mission.entity.Mission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllByScenarioIdAndActiveTrueOrderByIdAsc(Long scenarioId);
    List<Mission> findAllByScenarioIdAndDifficultyAndActiveTrueOrderByIdAsc(Long scenarioId, String difficulty);
}

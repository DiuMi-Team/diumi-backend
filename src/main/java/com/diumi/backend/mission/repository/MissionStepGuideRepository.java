package com.diumi.backend.mission.repository;

import com.diumi.backend.mission.entity.MissionStepGuide;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionStepGuideRepository extends JpaRepository<MissionStepGuide, Long> {
    Optional<MissionStepGuide> findByMissionIdAndStepId(Long missionId, Long stepId);
}

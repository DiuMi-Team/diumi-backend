package com.diumi.backend.mission.service;

import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.dto.MissionListResponse;
import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.mission.entity.MissionDifficulty;
import com.diumi.backend.mission.repository.MissionRepository;
import com.diumi.backend.scenario.repository.ScenarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MissionQueryService {
    private final ScenarioRepository scenarioRepository;
    private final MissionRepository missionRepository;

    public MissionQueryService(ScenarioRepository scenarioRepository, MissionRepository missionRepository) {
        this.scenarioRepository = scenarioRepository;
        this.missionRepository = missionRepository;
    }

    @Transactional(readOnly = true)
    public MissionListResponse getMissions(Long scenarioId, MissionDifficulty difficulty) {
        if (scenarioRepository.findByIdAndActiveTrue(scenarioId).isEmpty()) {
            throw new BusinessException(ErrorCode.SCENARIO_NOT_FOUND);
        }

        List<Mission> missions = difficulty == null
                ? missionRepository.findAllByScenarioIdAndActiveTrueOrderByIdAsc(scenarioId)
                : missionRepository.findAllByScenarioIdAndDifficultyAndActiveTrueOrderByIdAsc(
                        scenarioId, difficulty.name());
        return new MissionListResponse(missions.stream().map(this::toSummary).toList());
    }

    private MissionListResponse.MissionSummary toSummary(Mission mission) {
        return new MissionListResponse.MissionSummary(mission.getId(), mission.getTitle(),
                mission.getDescription(), mission.getDifficulty(), mission.getEstimatedMinutes());
    }
}

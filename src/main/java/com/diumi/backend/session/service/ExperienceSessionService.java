package com.diumi.backend.session.service;

import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.mission.repository.MissionRepository;
import com.diumi.backend.mission.repository.MissionStepGuideRepository;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioRepository;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.dto.StartSessionRequest;
import com.diumi.backend.session.dto.StartSessionResponse;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperienceSessionService {
    private final ScenarioRepository scenarioRepository;
    private final ScenarioStepRepository scenarioStepRepository;
    private final MissionRepository missionRepository;
    private final MissionStepGuideRepository missionStepGuideRepository;
    private final ExperienceSessionRepository sessionRepository;
    private final Clock clock;

    public ExperienceSessionService(ScenarioRepository scenarioRepository,
                                    ScenarioStepRepository scenarioStepRepository,
                                    MissionRepository missionRepository,
                                    MissionStepGuideRepository missionStepGuideRepository,
                                    ExperienceSessionRepository sessionRepository,
                                    Clock clock) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioStepRepository = scenarioStepRepository;
        this.missionRepository = missionRepository;
        this.missionStepGuideRepository = missionStepGuideRepository;
        this.sessionRepository = sessionRepository;
        this.clock = clock;
    }

    @Transactional
    public StartSessionResponse start(AnonymousVisitor visitor, StartSessionRequest request) {
        Scenario scenario = scenarioRepository.findById(request.scenarioId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCENARIO_NOT_FOUND));
        Mission mission = resolveMission(request, scenario);
        ScenarioStep firstStep = scenarioStepRepository.findFirstByScenarioIdOrderByStepOrderAsc(scenario.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REQUEST));
        long totalSteps = scenarioStepRepository.countByScenarioId(scenario.getId());

        LocalDateTime startedAt = LocalDateTime.now(clock);
        ExperienceSession session = sessionRepository.save(new ExperienceSession(
                UUID.randomUUID(), scenario, mission, visitor, request.sessionType(), startedAt));

        StartSessionResponse.CurrentStep currentStep = createCurrentStep(request, mission, firstStep);
        return new StartSessionResponse(
                session.getId(), scenario.getId(), mission == null ? null : mission.getId(),
                request.sessionType(), session.getStatus(),
                startedAt.atZone(TimeConfig.KST).toOffsetDateTime(), totalSteps,
                !request.sessionType().isPractice(), currentStep);
    }

    private Mission resolveMission(StartSessionRequest request, Scenario scenario) {
        if (request.sessionType().requiresMission() && request.missionId() == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        if (!request.sessionType().requiresMission() && request.missionId() != null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        if (!request.sessionType().requiresMission()) {
            return null;
        }

        Mission mission = missionRepository.findById(request.missionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MISSION_NOT_FOUND));
        if (!mission.getScenarioId().equals(scenario.getId())) {
            throw new BusinessException(ErrorCode.MISSION_SCENARIO_MISMATCH);
        }
        return mission;
    }

    private StartSessionResponse.CurrentStep createCurrentStep(StartSessionRequest request, Mission mission,
                                                                ScenarioStep firstStep) {
        if (!request.sessionType().isPractice()) {
            return new StartSessionResponse.CurrentStep(firstStep.getId(), firstStep.getStepOrder(),
                    firstStep.getStepName(), null, null);
        }

        MissionStepGuide guide = missionStepGuideRepository
                .findByMissionIdAndStepId(mission.getId(), firstStep.getId())
                .orElse(null);
        String guideMessage = guide == null ? firstStep.getGuideMessage() : guide.getGuideMessage();
        String highlightTargetCode = guide == null ? null : guide.getHighlightTargetCode();
        return new StartSessionResponse.CurrentStep(firstStep.getId(), firstStep.getStepOrder(),
                firstStep.getStepName(), guideMessage, highlightTargetCode);
    }
}

package com.diumi.backend.stepresult.service;

import com.diumi.backend.action.entity.ActionType;
import com.diumi.backend.action.repository.UserActionRepository;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.mission.repository.MissionStepGuideRepository;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionStatus;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.stepresult.dto.CompleteStepRequest;
import com.diumi.backend.stepresult.dto.CompleteStepResponse;
import com.diumi.backend.stepresult.entity.StepResult;
import com.diumi.backend.stepresult.repository.StepResultRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StepResultService {
    private final ExperienceSessionRepository sessionRepository;
    private final ScenarioStepRepository stepRepository;
    private final StepResultRepository resultRepository;
    private final UserActionRepository actionRepository;
    private final MissionStepGuideRepository guideRepository;
    private final Clock clock;

    public StepResultService(ExperienceSessionRepository sessionRepository,
                             ScenarioStepRepository stepRepository,
                             StepResultRepository resultRepository,
                             UserActionRepository actionRepository,
                             MissionStepGuideRepository guideRepository,
                             Clock clock) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.resultRepository = resultRepository;
        this.actionRepository = actionRepository;
        this.guideRepository = guideRepository;
        this.clock = clock;
    }

    @Transactional
    public CompleteStepResponse complete(UUID sessionId, Long stepId, AnonymousVisitor visitor,
                                         CompleteStepRequest request) {
        ExperienceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        validateSession(session, visitor);

        ScenarioStep step = stepRepository.findById(stepId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STEP_NOT_FOUND));
        if (!step.getScenarioId().equals(session.getScenarioId())) {
            throw new BusinessException(ErrorCode.STEP_NOT_FOUND);
        }
        if (resultRepository.existsBySessionIdAndStepId(sessionId, stepId)) {
            throw new BusinessException(ErrorCode.STEP_ALREADY_COMPLETED);
        }

        int errorCount = Math.toIntExact(actionRepository.countInvalidActions(sessionId, stepId));
        int aiHelpCount = Math.toIntExact(
                actionRepository.countActionsByType(sessionId, stepId, ActionType.AI_HELP));
        StepResult result = saveResult(new StepResult(session, step, request.elapsedMs(), errorCount,
                aiHelpCount, LocalDateTime.now(clock)));

        ScenarioStep next = findNextStep(session, step);
        return new CompleteStepResponse(result.getId(), step.getId(), request.elapsedMs(),
                errorCount, aiHelpCount, toNextStep(session, next), next == null);
    }

    private void validateSession(ExperienceSession session, AnonymousVisitor visitor) {
        if (!session.getVisitorId().equals(visitor.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_SESSION);
        }
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.SESSION_ALREADY_ENDED);
        }
    }

    private StepResult saveResult(StepResult result) {
        try {
            return resultRepository.saveAndFlush(result);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.STEP_ALREADY_COMPLETED);
        }
    }

    private ScenarioStep findNextStep(ExperienceSession session, ScenarioStep completedStep) {
        if (session.getType() != SessionType.REAL_FREE) {
            return stepRepository
                    .findFirstByScenarioIdAndStepOrderGreaterThanOrderByStepOrderAsc(
                            session.getScenarioId(), completedStep.getStepOrder())
                    .orElse(null);
        }

        Set<Long> completedIds = new HashSet<>(resultRepository.findCompletedStepIds(session.getId()));
        completedIds.add(completedStep.getId());
        return stepRepository.findAllByScenarioIdOrderByStepOrderAsc(session.getScenarioId()).stream()
                .filter(candidate -> !completedIds.contains(candidate.getId()))
                .findFirst()
                .orElse(null);
    }

    private CompleteStepResponse.NextStep toNextStep(ExperienceSession session, ScenarioStep next) {
        if (next == null) {
            return null;
        }
        if (session.getType() != SessionType.PRACTICE_MISSION) {
            return new CompleteStepResponse.NextStep(next.getId(), next.getStepOrder(),
                    next.getStepName(), null, null);
        }

        Optional<MissionStepGuide> guide = guideRepository
                .findByMissionIdAndStepId(session.getMission().getId(), next.getId());
        return new CompleteStepResponse.NextStep(next.getId(), next.getStepOrder(), next.getStepName(),
                guide.map(MissionStepGuide::getGuideMessage).orElse(next.getGuideMessage()),
                guide.map(MissionStepGuide::getHighlightTargetCode).orElse(null));
    }
}

package com.diumi.backend.session.service;

import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.action.repository.UserActionRepository;
import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.mission.repository.MissionStepGuideRepository;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.dto.SessionDetailResponse;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionStatus;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.stepresult.repository.StepResultRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionQueryService {
    private final ExperienceSessionRepository sessionRepository;
    private final ScenarioStepRepository stepRepository;
    private final StepResultRepository resultRepository;
    private final UserActionRepository actionRepository;
    private final MissionStepGuideRepository guideRepository;

    public SessionQueryService(ExperienceSessionRepository sessionRepository,
                               ScenarioStepRepository stepRepository,
                               StepResultRepository resultRepository,
                               UserActionRepository actionRepository,
                               MissionStepGuideRepository guideRepository) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.resultRepository = resultRepository;
        this.actionRepository = actionRepository;
        this.guideRepository = guideRepository;
    }

    @Transactional(readOnly = true)
    public SessionDetailResponse get(UUID sessionId, AnonymousVisitor visitor) {
        ExperienceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        if (!session.getVisitorId().equals(visitor.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_SESSION);
        }

        List<Long> completedStepIds = resultRepository.findCompletedStepIds(sessionId);
        List<ScenarioStep> steps = stepRepository.findAllByScenarioIdOrderByStepOrderAsc(
                session.getScenarioId());
        ScenarioStep current = resolveCurrentStep(session, steps, completedStepIds);

        return new SessionDetailResponse(
                session.getId(), session.getScenarioId(), session.getMissionId(), session.getType(),
                session.getStatus(), toOffsetDateTime(session.getStartedAt()),
                toOffsetDateTime(session.getEndedAt()), List.copyOf(completedStepIds), steps.size(),
                toCurrentStep(session, current));
    }

    private ScenarioStep resolveCurrentStep(ExperienceSession session, List<ScenarioStep> steps,
                                            List<Long> completedStepIds) {
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            return null;
        }
        if (session.getType() == SessionType.REAL_FREE) {
            return actionRepository.findFirstBySessionIdOrderByOccurredAtDescIdDesc(session.getId())
                    .map(UserAction::getStep)
                    .orElseGet(() -> steps.isEmpty() ? null : steps.get(0));
        }

        Set<Long> completed = new HashSet<>(completedStepIds);
        return steps.stream()
                .filter(step -> !completed.contains(step.getId()))
                .findFirst()
                .orElse(null);
    }

    private SessionDetailResponse.CurrentStep toCurrentStep(ExperienceSession session, ScenarioStep step) {
        if (step == null) {
            return null;
        }
        if (session.getType() != SessionType.PRACTICE_MISSION) {
            return new SessionDetailResponse.CurrentStep(step.getId(), step.getStepOrder(),
                    step.getStepName(), null, null);
        }

        Optional<MissionStepGuide> guide = guideRepository
                .findByMissionIdAndStepId(session.getMission().getId(), step.getId());
        return new SessionDetailResponse.CurrentStep(step.getId(), step.getStepOrder(), step.getStepName(),
                guide.map(MissionStepGuide::getGuideMessage).orElse(step.getGuideMessage()),
                guide.map(MissionStepGuide::getHighlightTargetCode).orElse(null));
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(TimeConfig.KST).toOffsetDateTime();
    }
}

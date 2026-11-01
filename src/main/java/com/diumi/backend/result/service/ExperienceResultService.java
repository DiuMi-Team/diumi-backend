package com.diumi.backend.result.service;

import com.diumi.backend.action.entity.ActionType;
import com.diumi.backend.action.repository.UserActionRepository;
import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.result.dto.EndSessionRequest;
import com.diumi.backend.result.dto.EndSessionResponse;
import com.diumi.backend.result.dto.SessionResultResponse;
import com.diumi.backend.result.entity.ExperienceResult;
import com.diumi.backend.result.repository.ExperienceResultRepository;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionStatus;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.stepresult.entity.StepResult;
import com.diumi.backend.stepresult.repository.StepResultRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperienceResultService {
    private final ExperienceSessionRepository sessionRepository;
    private final ScenarioStepRepository stepRepository;
    private final StepResultRepository stepResultRepository;
    private final UserActionRepository actionRepository;
    private final ExperienceResultRepository resultRepository;
    private final Clock clock;

    public ExperienceResultService(ExperienceSessionRepository sessionRepository,
                                   ScenarioStepRepository stepRepository,
                                   StepResultRepository stepResultRepository,
                                   UserActionRepository actionRepository,
                                   ExperienceResultRepository resultRepository,
                                   Clock clock) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.stepResultRepository = stepResultRepository;
        this.actionRepository = actionRepository;
        this.resultRepository = resultRepository;
        this.clock = clock;
    }

    @Transactional
    public EndSessionResponse end(UUID sessionId, AnonymousVisitor visitor, EndSessionRequest request) {
        if (request.status() == SessionStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        ExperienceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        validateSession(session, visitor);

        LocalDateTime endedAt = LocalDateTime.now(clock);
        boolean resultAvailable = request.status() == SessionStatus.COMPLETED;
        if (resultAvailable) {
            validateAllStepsCompleted(session);
            resultRepository.save(createResult(session, endedAt));
        }
        session.end(request.status(), endedAt);

        return new EndSessionResponse(session.getId(), session.getStatus(),
                endedAt.atZone(TimeConfig.KST).toOffsetDateTime(), resultAvailable);
    }

    @Transactional(readOnly = true)
    public SessionResultResponse getResult(UUID sessionId, AnonymousVisitor visitor) {
        ExperienceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        validateOwner(session, visitor);
        ExperienceResult result = resultRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESULT_NOT_FOUND));

        List<SessionResultResponse.WrongStep> wrongSteps = session.getType() == SessionType.REAL_FREE
                ? List.of()
                : stepResultRepository.findWrongStepResults(sessionId).stream()
                        .map(this::toWrongStep)
                        .toList();
        String missionTitle = session.getMission() == null ? null : session.getMission().getTitle();

        return new SessionResultResponse(session.getId(), session.getType(), session.getScenarioTitle(),
                missionTitle, result.getSuccess(), result.getElapsedSeconds(), result.getAiHelpCount(),
                result.getErrorCount(), result.getBackCount(), wrongSteps);
    }

    private SessionResultResponse.WrongStep toWrongStep(StepResult stepResult) {
        return new SessionResultResponse.WrongStep(stepResult.getStep().getId(),
                stepResult.getStep().getStepName(), stepResult.getErrorCount());
    }

    private void validateOwner(ExperienceSession session, AnonymousVisitor visitor) {
        if (!session.getVisitorId().equals(visitor.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_SESSION);
        }
    }

    private void validateSession(ExperienceSession session, AnonymousVisitor visitor) {
        validateOwner(session, visitor);
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.SESSION_ALREADY_ENDED);
        }
    }

    private void validateAllStepsCompleted(ExperienceSession session) {
        if (!session.getType().requiresMission()) {
            return;
        }
        long totalSteps = stepRepository.countByScenarioId(session.getScenarioId());
        long completedSteps = stepResultRepository.findCompletedStepIds(session.getId()).size();
        if (completedSteps < totalSteps) {
            throw new BusinessException(ErrorCode.SESSION_NOT_READY_TO_COMPLETE);
        }
    }

    private ExperienceResult createResult(ExperienceSession session, LocalDateTime endedAt) {
        UUID sessionId = session.getId();
        boolean missionSession = session.getType() != SessionType.REAL_FREE;

        Boolean success = missionSession ? isMissionSuccess(session) : null;
        Integer errorCount = missionSession
                ? Math.toIntExact(actionRepository.countInvalidActionsInSession(sessionId))
                : null;
        int backCount = Math.toIntExact(actionRepository.countActionsInSessionByType(sessionId, ActionType.BACK));
        int aiHelpCount = Math.toIntExact(
                actionRepository.countActionsInSessionByType(sessionId, ActionType.AI_HELP));
        int elapsedSeconds = Math.toIntExact(Duration.between(session.getStartedAt(), endedAt).getSeconds());

        return new ExperienceResult(sessionId, success, Math.max(elapsedSeconds, 0), errorCount,
                backCount, aiHelpCount, endedAt);
    }

    /**
     * 모든 단계에서 정답 행동을 한 번 이상 했으면 미션 성공으로 본다.
     * 중간에 틀렸더라도 결국 정답을 골랐다면 성공이다.
     */
    private boolean isMissionSuccess(ExperienceSession session) {
        long totalSteps = stepRepository.countByScenarioId(session.getScenarioId());
        long stepsWithValidAction = actionRepository.countStepsWithValidAction(session.getId());
        return totalSteps > 0 && stepsWithValidAction >= totalSteps;
    }
}

package com.diumi.backend.ai.service;

import com.diumi.backend.action.entity.ActionType;
import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.action.repository.UserActionRepository;
import com.diumi.backend.ai.client.OpenAiRealtimeClient;
import com.diumi.backend.ai.client.OpenAiRealtimeClient.ClientSecret;
import com.diumi.backend.ai.client.OpenAiRealtimeClientException;
import com.diumi.backend.ai.dto.StartAiConnectionRequest;
import com.diumi.backend.ai.dto.StartAiConnectionResponse;
import com.diumi.backend.ai.guide.entity.AiGuideRule;
import com.diumi.backend.ai.guide.repository.AiGuideRuleRepository;
import com.diumi.backend.ai.prompt.AiMissionContextFactory;
import com.diumi.backend.ai.prompt.AiPromptFactory;
import com.diumi.backend.common.config.TimeConfig;
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
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiConnectionService {
    private final ExperienceSessionRepository sessionRepository;
    private final ScenarioStepRepository stepRepository;
    private final MissionStepGuideRepository missionStepGuideRepository;
    private final AiGuideRuleRepository aiGuideRuleRepository;
    private final UserActionRepository actionRepository;
    private final OpenAiRealtimeClient realtimeClient;
    private final AiPromptFactory promptFactory;
    private final AiMissionContextFactory missionContextFactory;
    private final Clock clock;

    public AiConnectionService(ExperienceSessionRepository sessionRepository,
                               ScenarioStepRepository stepRepository,
                               MissionStepGuideRepository missionStepGuideRepository,
                               AiGuideRuleRepository aiGuideRuleRepository,
                               UserActionRepository actionRepository,
                               OpenAiRealtimeClient realtimeClient,
                               AiPromptFactory promptFactory,
                               AiMissionContextFactory missionContextFactory,
                               Clock clock) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.missionStepGuideRepository = missionStepGuideRepository;
        this.aiGuideRuleRepository = aiGuideRuleRepository;
        this.actionRepository = actionRepository;
        this.realtimeClient = realtimeClient;
        this.promptFactory = promptFactory;
        this.missionContextFactory = missionContextFactory;
        this.clock = clock;
    }

    @Transactional
    public StartAiConnectionResponse start(UUID sessionId, AnonymousVisitor visitor,
                                           StartAiConnectionRequest request) {
        ExperienceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        validateSession(session, visitor);

        ScenarioStep step = stepRepository.findById(request.stepId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STEP_NOT_FOUND));
        if (!step.getScenarioId().equals(session.getScenarioId())) {
            throw new BusinessException(ErrorCode.STEP_NOT_FOUND);
        }

        ClientSecret secret;
        try {
            MissionStepGuide missionGuide = findMissionGuide(session, step);
            List<AiGuideRule> guideRules = aiGuideRuleRepository
                    .findByScenarioTypeAndScreenCodeInAndActiveTrueOrderByPriorityAscIdAsc(
                            session.getScenarioType(), List.of("COMMON", step.getStepCode()));
            secret = realtimeClient.createClientSecret(visitor.getId().toString(),
                    promptFactory.create(session, step, missionGuide,
                            missionContextFactory.create(session, step), guideRules));
        } catch (OpenAiRealtimeClientException exception) {
            throw new BusinessException(ErrorCode.AI_CONNECTION_FAILED);
        }

        actionRepository.save(new UserAction(session, step, ActionType.AI_HELP,
                null, null, null, LocalDateTime.now(clock)));
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(
                Instant.ofEpochSecond(secret.expiresAt()), TimeConfig.KST);
        return new StartAiConnectionResponse(secret.value(), expiresAt,
                secret.model(), secret.conversationId());
    }

    private void validateSession(ExperienceSession session, AnonymousVisitor visitor) {
        if (!session.getVisitorId().equals(visitor.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_SESSION);
        }
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.SESSION_ALREADY_ENDED);
        }
        if (session.getType() == SessionType.PRACTICE_MISSION) {
            throw new BusinessException(ErrorCode.AI_NOT_ALLOWED);
        }
    }

    private MissionStepGuide findMissionGuide(ExperienceSession session, ScenarioStep step) {
        if (session.getMissionId() == null) {
            return null;
        }
        return missionStepGuideRepository.findByMissionIdAndStepId(
                session.getMissionId(), step.getId()).orElse(null);
    }
}

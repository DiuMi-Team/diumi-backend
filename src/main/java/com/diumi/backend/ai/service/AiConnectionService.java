package com.diumi.backend.ai.service;

import com.diumi.backend.action.entity.ActionType;
import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.action.repository.UserActionRepository;
import com.diumi.backend.ai.client.OpenAiRealtimeClient;
import com.diumi.backend.ai.client.OpenAiRealtimeClient.ClientSecret;
import com.diumi.backend.ai.client.OpenAiRealtimeClientException;
import com.diumi.backend.ai.dto.StartAiConnectionRequest;
import com.diumi.backend.ai.dto.StartAiConnectionResponse;
import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
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
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiConnectionService {
    private final ExperienceSessionRepository sessionRepository;
    private final ScenarioStepRepository stepRepository;
    private final UserActionRepository actionRepository;
    private final OpenAiRealtimeClient realtimeClient;
    private final Clock clock;

    public AiConnectionService(ExperienceSessionRepository sessionRepository,
                               ScenarioStepRepository stepRepository,
                               UserActionRepository actionRepository,
                               OpenAiRealtimeClient realtimeClient,
                               Clock clock) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.actionRepository = actionRepository;
        this.realtimeClient = realtimeClient;
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
            secret = realtimeClient.createClientSecret(visitor.getId().toString(),
                    createInstructions(session, step));
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

    private String createInstructions(ExperienceSession session, ScenarioStep step) {
        return """
                당신은 고령 사용자를 돕는 디지털 생활 음성 도우미입니다.
                현재 시나리오: %s
                현재 단계: %s
                사용자의 현재 단계 수행에 필요한 내용만 쉽고 짧은 한국어로 답하세요.
                한 번의 연결에서는 질문 하나에만 답하고, 모르는 내용은 추측하지 마세요.
                """.formatted(session.getScenarioTitle(), step.getStepName()).strip();
    }
}

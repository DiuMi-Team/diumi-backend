package com.diumi.backend.ai.service;

import com.diumi.backend.ai.dto.SaveAiMessagesRequest;
import com.diumi.backend.ai.dto.SaveAiMessagesResponse;
import com.diumi.backend.ai.entity.AiMessage;
import com.diumi.backend.ai.entity.AiMessageRole;
import com.diumi.backend.ai.repository.AiMessageRepository;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiMessageService {
    private final ExperienceSessionRepository sessionRepository;
    private final ScenarioStepRepository stepRepository;
    private final AiMessageRepository messageRepository;
    private final Clock clock;

    public AiMessageService(ExperienceSessionRepository sessionRepository,
                            ScenarioStepRepository stepRepository,
                            AiMessageRepository messageRepository,
                            Clock clock) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.messageRepository = messageRepository;
        this.clock = clock;
    }

    @Transactional
    public SaveAiMessagesResponse save(UUID sessionId, AnonymousVisitor visitor,
                                       SaveAiMessagesRequest request) {
        ExperienceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        validateSession(session, visitor);

        ScenarioStep step = stepRepository.findById(request.stepId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STEP_NOT_FOUND));
        if (!step.getScenarioId().equals(session.getScenarioId())) {
            throw new BusinessException(ErrorCode.STEP_NOT_FOUND);
        }

        LocalDateTime createdAt = LocalDateTime.now(clock);
        AiMessage question = new AiMessage(session, step, AiMessageRole.USER,
                request.question().strip(), null, createdAt);
        AiMessage answer = new AiMessage(session, step, AiMessageRole.AI,
                request.answer().strip(), request.responseTimeMs(), createdAt);
        List<AiMessage> saved = messageRepository.saveAllAndFlush(List.of(question, answer));

        return new SaveAiMessagesResponse(saved.get(0).getId(), saved.get(1).getId());
    }

    private void validateSession(ExperienceSession session, AnonymousVisitor visitor) {
        if (!session.getVisitorId().equals(visitor.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_SESSION);
        }
        if (session.getType() == SessionType.PRACTICE_MISSION) {
            throw new BusinessException(ErrorCode.AI_NOT_ALLOWED);
        }
    }
}

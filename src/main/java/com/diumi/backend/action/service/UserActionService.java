package com.diumi.backend.action.service;

import com.diumi.backend.action.dto.RecordActionRequest;
import com.diumi.backend.action.dto.RecordActionResponse;
import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.action.repository.UserActionRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserActionService {
    private static final long ALLOWED_FUTURE_SKEW_MINUTES = 5;
    // 브라우저 시계가 서버보다 조금 느려도 시작 직후 행동이 거절되지 않도록 허용하는 오차
    private static final long ALLOWED_PAST_SKEW_SECONDS = 60;

    private final ExperienceSessionRepository sessionRepository;
    private final ScenarioStepRepository stepRepository;
    private final MissionStepGuideRepository guideRepository;
    private final UserActionRepository actionRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public UserActionService(ExperienceSessionRepository sessionRepository,
                             ScenarioStepRepository stepRepository,
                             MissionStepGuideRepository guideRepository,
                             UserActionRepository actionRepository,
                             ObjectMapper objectMapper,
                             Clock clock) {
        this.sessionRepository = sessionRepository;
        this.stepRepository = stepRepository;
        this.guideRepository = guideRepository;
        this.actionRepository = actionRepository;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Transactional
    public RecordActionResponse record(UUID sessionId, AnonymousVisitor visitor, RecordActionRequest request) {
        ExperienceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        validateSession(session, visitor);

        ScenarioStep step = stepRepository.findById(request.stepId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STEP_NOT_FOUND));
        if (!step.getScenarioId().equals(session.getScenarioId())) {
            throw new BusinessException(ErrorCode.STEP_NOT_FOUND);
        }
        validateOccurredAt(session, request.occurredAt());

        Boolean valid = session.getType() == SessionType.REAL_FREE
                ? null
                : evaluateAnswer(session, step, request);
        LocalDateTime occurredAt = LocalDateTime.ofInstant(request.occurredAt().toInstant(), TimeConfig.KST);
        UserAction action = actionRepository.save(new UserAction(session, step, request.actionType(),
                request.targetCode(), valid, request.actionData(), occurredAt));

        Feedback feedback = resolveFeedback(session, step, valid);
        return new RecordActionResponse(action.getId(), valid,
                feedback.message(), feedback.highlightTargetCode());
    }

    private void validateSession(ExperienceSession session, AnonymousVisitor visitor) {
        if (!session.getVisitorId().equals(visitor.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_SESSION);
        }
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.SESSION_ALREADY_ENDED);
        }
    }

    private void validateOccurredAt(ExperienceSession session, java.time.OffsetDateTime occurredAt) {
        Instant occurredInstant = occurredAt.toInstant();
        Instant startedInstant = session.getStartedAt().atZone(TimeConfig.KST).toInstant()
                .truncatedTo(ChronoUnit.SECONDS)
                .minusSeconds(ALLOWED_PAST_SKEW_SECONDS);
        Instant latestAllowed = clock.instant().plus(ALLOWED_FUTURE_SKEW_MINUTES, ChronoUnit.MINUTES);
        if (occurredInstant.isBefore(startedInstant) || occurredInstant.isAfter(latestAllowed)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    }

    /**
     * 현재 단계의 정답 조건과 사용자 행동을 비교한다.
     * - 단계 정답이 객체 하나면 그 조건과 같아야 정답이다.
     * - 단계 정답에 accept 배열이 있으면 그중 하나와 같으면 정답이다.
     *   (예: 옵션 화면에서 ICE, Large, 수량 2, 담기 버튼은 모두 맞는 행동)
     * - 미션에 이 단계 정답이 없으면 채점하지 않는다(null).
     */
    private Boolean evaluateAnswer(ExperienceSession session, ScenarioStep step, RecordActionRequest request) {
        JsonNode expected = readTargetData(session).get(step.getStepCode());
        if (expected == null || expected.isNull()) {
            return null;
        }
        JsonNode accept = expected.get("accept");
        if (accept != null && accept.isArray()) {
            for (JsonNode candidate : accept) {
                if (matches(candidate, request)) {
                    return true;
                }
            }
            return false;
        }
        if (!expected.isObject() || expected.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        return matches(expected, request);
    }

    private boolean matches(JsonNode expected, RecordActionRequest request) {
        JsonNode expectedActionType = expected.get("action_type");
        JsonNode expectedTargetCode = expected.get("target_code");
        JsonNode expectedActionData = expected.get("action_data");
        if (expectedActionType == null && expectedTargetCode == null && expectedActionData == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        return matchesText(expectedActionType, request.actionType().name())
                && matchesText(expectedTargetCode, request.targetCode())
                && matchesSubset(expectedActionData, request.actionData());
    }

    private JsonNode readTargetData(ExperienceSession session) {
        try {
            return objectMapper.readTree(session.getMission().getTargetData());
        } catch (JacksonException | NullPointerException exception) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    }

    private boolean matchesText(JsonNode expected, String actual) {
        return expected == null || (expected.isString() && expected.asString().equals(actual));
    }

    private boolean matchesSubset(JsonNode expected, JsonNode actual) {
        if (expected == null) {
            return true;
        }
        if (actual == null) {
            return false;
        }
        if (!expected.isObject()) {
            return expected.equals(actual);
        }

        for (Map.Entry<String, JsonNode> field : expected.properties()) {
            if (!matchesSubset(field.getValue(), actual.get(field.getKey()))) {
                return false;
            }
        }
        return true;
    }

    private Feedback resolveFeedback(ExperienceSession session, ScenarioStep step, Boolean valid) {
        if (session.getType() != SessionType.PRACTICE_MISSION || !Boolean.FALSE.equals(valid)) {
            return Feedback.EMPTY;
        }

        MissionStepGuide guide = guideRepository
                .findByMissionIdAndStepId(session.getMission().getId(), step.getId())
                .orElse(null);
        if (guide == null) {
            return new Feedback(step.getGuideMessage(), null);
        }
        return new Feedback(guide.getGuideMessage(), guide.getHighlightTargetCode());
    }

    private record Feedback(String message, String highlightTargetCode) {
        private static final Feedback EMPTY = new Feedback(null, null);
    }
}

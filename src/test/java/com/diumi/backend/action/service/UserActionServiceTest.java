package com.diumi.backend.action.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diumi.backend.action.dto.RecordActionRequest;
import com.diumi.backend.action.dto.RecordActionResponse;
import com.diumi.backend.action.entity.ActionType;
import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.action.repository.UserActionRepository;
import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.mission.repository.MissionStepGuideRepository;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionStatus;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class UserActionServiceTest {
    private static final UUID SESSION_ID = UUID.fromString("a3d9e2c1-77b4-4f0e-9a51-0c2b8e6f1d24");
    private static final OffsetDateTime OCCURRED_AT = OffsetDateTime.parse("2026-10-26T14:31:20+09:00");

    @Mock ExperienceSessionRepository sessionRepository;
    @Mock ScenarioStepRepository stepRepository;
    @Mock MissionStepGuideRepository guideRepository;
    @Mock UserActionRepository actionRepository;

    private UserActionService service;
    private Scenario scenario;
    private ScenarioStep step;
    private AnonymousVisitor visitor;

    @BeforeEach
    void setUp() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-10-26T05:32:00Z"), TimeConfig.KST);
        service = new UserActionService(sessionRepository, stepRepository, guideRepository,
                actionRepository, new ObjectMapper(), clock);
        scenario = new Scenario(1L, "CAFE", "카페", "주문", "image.png", true);
        step = new ScenarioStep(scenario, "SELECT_MENU", 1, "메뉴 고르기", "메뉴를 골라 보세요.");
        setId(step, 11L);
        visitor = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        lenient().when(stepRepository.findById(11L)).thenReturn(Optional.of(step));
        lenient().when(actionRepository.save(any())).thenAnswer(invocation -> {
            UserAction action = invocation.getArgument(0);
            setId(action, 5001L);
            return action;
        });
    }

    @Test
    void practiceWrongAnswerReturnsGuideFeedback() throws Exception {
        Mission mission = mission("""
                {"SELECT_MENU":{"action_type":"SELECT","target_code":"MENU_AMERICANO"}}
                """);
        ExperienceSession session = session(mission, SessionType.PRACTICE_MISSION);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
        when(guideRepository.findByMissionIdAndStepId(101L, 11L)).thenReturn(Optional.of(
                new MissionStepGuide(mission, step, "앗, 아메리카노를 골라야 해요.", "MENU_AMERICANO")));

        RecordActionResponse response = service.record(SESSION_ID, visitor,
                request(ActionType.SELECT, "MENU_LATTE"));

        assertThat(response.actionId()).isEqualTo(5001L);
        assertThat(response.isValid()).isFalse();
        assertThat(response.feedbackMessage()).isEqualTo("앗, 아메리카노를 골라야 해요.");
        assertThat(response.highlightTargetCode()).isEqualTo("MENU_AMERICANO");
    }

    @Test
    void realMissionStoresCorrectnessWithoutFeedback() throws Exception {
        Mission mission = mission("""
                {"SELECT_MENU":{"action_type":"SELECT","target_code":"MENU_AMERICANO"}}
                """);
        when(sessionRepository.findById(SESSION_ID))
                .thenReturn(Optional.of(session(mission, SessionType.REAL_MISSION)));

        RecordActionResponse response = service.record(SESSION_ID, visitor,
                request(ActionType.SELECT, "MENU_AMERICANO"));

        assertThat(response.isValid()).isTrue();
        assertThat(response.feedbackMessage()).isNull();
        assertThat(response.highlightTargetCode()).isNull();
    }

    @Test
    void realFreeDoesNotEvaluateCorrectness() throws Exception {
        when(sessionRepository.findById(SESSION_ID))
                .thenReturn(Optional.of(session(null, SessionType.REAL_FREE)));

        RecordActionResponse response = service.record(SESSION_ID, visitor,
                request(ActionType.BACK, null));

        assertThat(response.isValid()).isNull();
        assertThat(response.feedbackMessage()).isNull();
    }

    @Test
    void rejectsAnotherVisitorsSession() throws Exception {
        Mission mission = mission("{} ");
        AnonymousVisitor owner = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        ExperienceSession session = new ExperienceSession(SESSION_ID, scenario, mission, owner,
                SessionType.REAL_MISSION, LocalDateTime.parse("2026-10-26T14:30:00"));
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        assertError(ErrorCode.FORBIDDEN_SESSION,
                () -> service.record(SESSION_ID, visitor, request(ActionType.SELECT, "MENU_AMERICANO")));
        verify(actionRepository, never()).save(any());
    }

    @Test
    void rejectsEndedSession() throws Exception {
        Mission mission = mission("{} ");
        ExperienceSession session = session(mission, SessionType.REAL_MISSION);
        setField(session, "status", SessionStatus.COMPLETED);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        assertError(ErrorCode.SESSION_ALREADY_ENDED,
                () -> service.record(SESSION_ID, visitor, request(ActionType.SELECT, "MENU_AMERICANO")));
    }

    @Test
    void rejectsStepFromDifferentScenario() throws Exception {
        Mission mission = mission("{} ");
        when(sessionRepository.findById(SESSION_ID))
                .thenReturn(Optional.of(session(mission, SessionType.REAL_MISSION)));
        Scenario otherScenario = new Scenario(2L, "TRAIN", "기차", "예매", "train.png", true);
        ScenarioStep otherStep = new ScenarioStep(otherScenario, "SELECT_TRAIN", 1, "기차 고르기", null);
        setId(otherStep, 11L);
        when(stepRepository.findById(11L)).thenReturn(Optional.of(otherStep));

        assertError(ErrorCode.STEP_NOT_FOUND,
                () -> service.record(SESSION_ID, visitor, request(ActionType.SELECT, "TRAIN_1")));
    }

    private Mission mission(String targetData) throws Exception {
        Mission mission = new Mission(scenario, "아메리카노", "주문", "EASY", targetData, true);
        setId(mission, 101L);
        return mission;
    }

    private ExperienceSession session(Mission mission, SessionType type) {
        return new ExperienceSession(SESSION_ID, scenario, mission, visitor, type,
                LocalDateTime.parse("2026-10-26T14:30:00"));
    }

    private RecordActionRequest request(ActionType type, String targetCode) {
        return new RecordActionRequest(11L, type, targetCode, null, OCCURRED_AT);
    }

    private static void assertError(ErrorCode expected, Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.errorCode()).isEqualTo(expected));
    }

    private static void setId(Object target, Long id) throws Exception {
        setField(target, "id", id);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}

package com.diumi.backend.stepresult.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.diumi.backend.action.entity.ActionType;
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
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.stepresult.dto.CompleteStepRequest;
import com.diumi.backend.stepresult.dto.CompleteStepResponse;
import com.diumi.backend.stepresult.entity.StepResult;
import com.diumi.backend.stepresult.repository.StepResultRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class StepResultServiceTest {
    private static final UUID SESSION_ID = UUID.fromString("a3d9e2c1-77b4-4f0e-9a51-0c2b8e6f1d24");

    @Mock ExperienceSessionRepository sessionRepository;
    @Mock ScenarioStepRepository stepRepository;
    @Mock StepResultRepository resultRepository;
    @Mock UserActionRepository actionRepository;
    @Mock MissionStepGuideRepository guideRepository;

    private StepResultService service;
    private Scenario scenario;
    private Mission mission;
    private ScenarioStep firstStep;
    private ScenarioStep secondStep;
    private AnonymousVisitor visitor;

    @BeforeEach
    void setUp() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-10-26T05:40:00Z"), TimeConfig.KST);
        service = new StepResultService(sessionRepository, stepRepository, resultRepository,
                actionRepository, guideRepository, clock);
        scenario = new Scenario(1L, "CAFE", "카페", "주문", "image.png", true);
        mission = new Mission(scenario, "아메리카노", "주문", "EASY", "{}", true);
        setId(mission, 101L);
        firstStep = step(11L, "SELECT_MENU", 1, "메뉴 고르기", "메뉴를 골라 보세요.");
        secondStep = step(12L, "SELECT_OPTION", 2, "옵션 고르기", "옵션을 골라 보세요.");
        visitor = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        lenient().when(stepRepository.findById(11L)).thenReturn(Optional.of(firstStep));
        lenient().when(resultRepository.existsBySessionIdAndStepId(SESSION_ID, 11L)).thenReturn(false);
        lenient().when(actionRepository.countInvalidActions(SESSION_ID, 11L)).thenReturn(1L);
        lenient().when(actionRepository.countActionsByType(SESSION_ID, 11L, ActionType.AI_HELP)).thenReturn(2L);
        lenient().when(resultRepository.saveAndFlush(any())).thenAnswer(invocation -> {
            StepResult result = invocation.getArgument(0);
            setId(result, 7001L);
            return result;
        });
    }

    @Test
    void completesPracticeStepAndReturnsNextGuide() {
        ExperienceSession session = session(SessionType.PRACTICE_MISSION);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
        when(stepRepository.findFirstByScenarioIdAndStepOrderGreaterThanOrderByStepOrderAsc(1L, 1))
                .thenReturn(Optional.of(secondStep));
        when(guideRepository.findByMissionIdAndStepId(101L, 12L)).thenReturn(Optional.of(
                new MissionStepGuide(mission, secondStep, "차가운 것을 골라 보세요.", "OPT_ICE")));

        CompleteStepResponse response = service.complete(SESSION_ID, 11L, visitor,
                new CompleteStepRequest(18_500));

        assertThat(response.stepResultId()).isEqualTo(7001L);
        assertThat(response.stepId()).isEqualTo(11L);
        assertThat(response.elapsedMs()).isEqualTo(18_500);
        assertThat(response.errorCount()).isEqualTo(1);
        assertThat(response.aiHelpCount()).isEqualTo(2);
        assertThat(response.isLastStep()).isFalse();
        assertThat(response.nextStep().stepId()).isEqualTo(12L);
        assertThat(response.nextStep().guideMessage()).isEqualTo("차가운 것을 골라 보세요.");
        assertThat(response.nextStep().highlightTargetCode()).isEqualTo("OPT_ICE");
    }

    @Test
    void realMissionLastStepReturnsNullNextStep() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_MISSION)));
        when(stepRepository.findFirstByScenarioIdAndStepOrderGreaterThanOrderByStepOrderAsc(1L, 1))
                .thenReturn(Optional.empty());

        CompleteStepResponse response = service.complete(SESSION_ID, 11L, visitor,
                new CompleteStepRequest(5_000));

        assertThat(response.nextStep()).isNull();
        assertThat(response.isLastStep()).isTrue();
    }

    @Test
    void realFreeReturnsSmallestUncompletedStep() throws Exception {
        ScenarioStep thirdStep = step(13L, "PAY", 3, "결제하기", null);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_FREE)));
        when(resultRepository.findCompletedStepIds(SESSION_ID)).thenReturn(List.of(11L));
        when(stepRepository.findAllByScenarioIdOrderByStepOrderAsc(1L))
                .thenReturn(List.of(firstStep, secondStep, thirdStep));

        CompleteStepResponse response = service.complete(SESSION_ID, 11L, visitor,
                new CompleteStepRequest(5_000));

        assertThat(response.nextStep().stepId()).isEqualTo(12L);
        assertThat(response.nextStep().guideMessage()).isNull();
        assertThat(response.isLastStep()).isFalse();
    }

    @Test
    void rejectsAlreadyCompletedStep() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_MISSION)));
        when(resultRepository.existsBySessionIdAndStepId(SESSION_ID, 11L)).thenReturn(true);

        assertError(ErrorCode.STEP_ALREADY_COMPLETED,
                () -> service.complete(SESSION_ID, 11L, visitor, new CompleteStepRequest(1_000)));
    }

    @Test
    void mapsConcurrentUniqueConflictToAlreadyCompleted() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_MISSION)));
        doThrow(new DataIntegrityViolationException("duplicate"))
                .when(resultRepository).saveAndFlush(any());

        assertError(ErrorCode.STEP_ALREADY_COMPLETED,
                () -> service.complete(SESSION_ID, 11L, visitor, new CompleteStepRequest(1_000)));
    }

    @Test
    void rejectsAnotherVisitorsSession() {
        AnonymousVisitor owner = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        ExperienceSession session = new ExperienceSession(SESSION_ID, scenario, mission, owner,
                SessionType.REAL_MISSION, LocalDateTime.now());
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        assertError(ErrorCode.FORBIDDEN_SESSION,
                () -> service.complete(SESSION_ID, 11L, visitor, new CompleteStepRequest(1_000)));
    }

    private ExperienceSession session(SessionType type) {
        return new ExperienceSession(SESSION_ID, scenario, type == SessionType.REAL_FREE ? null : mission,
                visitor, type, LocalDateTime.parse("2026-10-26T14:30:00"));
    }

    private ScenarioStep step(Long id, String code, int order, String name, String guide) throws Exception {
        ScenarioStep step = new ScenarioStep(scenario, code, order, name, guide);
        setId(step, id);
        return step;
    }

    private static void assertError(ErrorCode expected, Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.errorCode()).isEqualTo(expected));
    }

    private static void setId(Object target, Long id) throws Exception {
        Field field = target.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(target, id);
    }
}

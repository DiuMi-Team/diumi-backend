package com.diumi.backend.session.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diumi.backend.action.entity.ActionType;
import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.action.repository.UserActionRepository;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.mission.repository.MissionStepGuideRepository;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.dto.SessionDetailResponse;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionStatus;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.stepresult.repository.StepResultRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionQueryServiceTest {
    private static final UUID SESSION_ID = UUID.fromString("a3d9e2c1-77b4-4f0e-9a51-0c2b8e6f1d24");

    @Mock ExperienceSessionRepository sessionRepository;
    @Mock ScenarioStepRepository stepRepository;
    @Mock StepResultRepository resultRepository;
    @Mock UserActionRepository actionRepository;
    @Mock MissionStepGuideRepository guideRepository;

    private SessionQueryService service;
    private AnonymousVisitor visitor;
    private Scenario scenario;
    private Mission mission;
    private ScenarioStep firstStep;
    private ScenarioStep secondStep;

    @BeforeEach
    void setUp() throws Exception {
        service = new SessionQueryService(sessionRepository, stepRepository, resultRepository,
                actionRepository, guideRepository);
        visitor = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        scenario = new Scenario(1L, "CAFE", "카페 주문", "주문", "image.png", true);
        mission = new Mission(scenario, "아메리카노", "주문", "EASY", "{}", true);
        setId(mission, 101L);
        firstStep = step(11L, "SELECT_MENU", 1, "메뉴 고르기", "메뉴를 골라 보세요.");
        secondStep = step(12L, "SELECT_OPTION", 2, "옵션 고르기", "옵션을 골라 보세요.");
    }

    @Test
    void practiceMissionRestoresEarliestUncompletedStepWithGuide() {
        ExperienceSession session = session(SessionType.PRACTICE_MISSION);
        stubSessionData(session, List.of(11L), List.of(firstStep, secondStep));
        when(guideRepository.findByMissionIdAndStepId(101L, 12L)).thenReturn(Optional.of(
                new MissionStepGuide(mission, secondStep, "차가운 것을 골라 보세요.", "OPT_ICE")));

        SessionDetailResponse response = service.get(SESSION_ID, visitor);

        assertThat(response.sessionId()).isEqualTo(SESSION_ID);
        assertThat(response.scenarioId()).isEqualTo(1L);
        assertThat(response.missionId()).isEqualTo(101L);
        assertThat(response.completedStepIds()).containsExactly(11L);
        assertThat(response.totalSteps()).isEqualTo(2);
        assertThat(response.currentStep().stepId()).isEqualTo(12L);
        assertThat(response.currentStep().guideMessage()).isEqualTo("차가운 것을 골라 보세요.");
        assertThat(response.currentStep().highlightTargetCode()).isEqualTo("OPT_ICE");
        assertThat(response.startedAt().getOffset().getTotalSeconds()).isEqualTo(9 * 3600);
        assertThat(response.endedAt()).isNull();
    }

    @Test
    void realMissionHidesGuide() {
        stubSessionData(session(SessionType.REAL_MISSION), List.of(), List.of(firstStep, secondStep));

        SessionDetailResponse response = service.get(SESSION_ID, visitor);

        assertThat(response.currentStep().stepId()).isEqualTo(11L);
        assertThat(response.currentStep().guideMessage()).isNull();
        assertThat(response.currentStep().highlightTargetCode()).isNull();
    }

    @Test
    void realFreeRestoresLastActionStep() {
        ExperienceSession session = session(SessionType.REAL_FREE);
        stubSessionData(session, List.of(11L), List.of(firstStep, secondStep));
        UserAction latestAction = new UserAction(session, secondStep, ActionType.OPTION,
                "OPT_ICE", null, null, LocalDateTime.parse("2026-10-26T14:40:00"));
        when(actionRepository.findFirstBySessionIdOrderByOccurredAtDescIdDesc(SESSION_ID))
                .thenReturn(Optional.of(latestAction));

        SessionDetailResponse response = service.get(SESSION_ID, visitor);

        assertThat(response.currentStep().stepId()).isEqualTo(12L);
        assertThat(response.currentStep().guideMessage()).isNull();
    }

    @Test
    void realFreeWithoutActionsRestoresFirstStep() {
        stubSessionData(session(SessionType.REAL_FREE), List.of(), List.of(firstStep, secondStep));
        when(actionRepository.findFirstBySessionIdOrderByOccurredAtDescIdDesc(SESSION_ID))
                .thenReturn(Optional.empty());

        SessionDetailResponse response = service.get(SESSION_ID, visitor);

        assertThat(response.currentStep().stepId()).isEqualTo(11L);
    }

    @Test
    void completedSessionHasNoCurrentStep() throws Exception {
        ExperienceSession session = session(SessionType.REAL_MISSION);
        setField(session, "status", SessionStatus.COMPLETED);
        setField(session, "endedAt", LocalDateTime.parse("2026-10-26T14:50:00"));
        stubSessionData(session, List.of(11L, 12L), List.of(firstStep, secondStep));

        SessionDetailResponse response = service.get(SESSION_ID, visitor);

        assertThat(response.currentStep()).isNull();
        assertThat(response.endedAt()).isNotNull();
        assertThat(response.completedStepIds()).containsExactly(11L, 12L);
        verify(actionRepository, never()).findFirstBySessionIdOrderByOccurredAtDescIdDesc(SESSION_ID);
    }

    @Test
    void rejectsAnotherVisitorsSession() {
        AnonymousVisitor owner = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        ExperienceSession session = new ExperienceSession(SESSION_ID, scenario, mission, owner,
                SessionType.REAL_MISSION, LocalDateTime.now());
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> service.get(SESSION_ID, visitor))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.FORBIDDEN_SESSION));
        verify(resultRepository, never()).findCompletedStepIds(SESSION_ID);
    }

    private void stubSessionData(ExperienceSession session, List<Long> completed,
                                 List<ScenarioStep> steps) {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
        when(resultRepository.findCompletedStepIds(SESSION_ID)).thenReturn(completed);
        when(stepRepository.findAllByScenarioIdOrderByStepOrderAsc(1L)).thenReturn(steps);
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

    private static void setId(Object target, Long id) throws Exception {
        setField(target, "id", id);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}

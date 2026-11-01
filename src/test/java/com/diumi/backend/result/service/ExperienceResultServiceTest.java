package com.diumi.backend.result.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diumi.backend.action.entity.ActionType;
import com.diumi.backend.action.repository.UserActionRepository;
import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.result.dto.EndSessionRequest;
import com.diumi.backend.result.dto.EndSessionResponse;
import com.diumi.backend.result.dto.SessionResultResponse;
import com.diumi.backend.result.entity.ExperienceResult;
import com.diumi.backend.result.repository.ExperienceResultRepository;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionStatus;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExperienceResultServiceTest {
    @Mock ExperienceSessionRepository sessionRepository;
    @Mock ScenarioStepRepository stepRepository;
    @Mock StepResultRepository stepResultRepository;
    @Mock UserActionRepository actionRepository;
    @Mock ExperienceResultRepository resultRepository;

    private ExperienceResultService service;
    private Scenario scenario;
    private Mission mission;
    private AnonymousVisitor visitor;
    private UUID sessionId;

    @BeforeEach
    void setUp() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-10-06T05:33:10Z"), TimeConfig.KST);
        service = new ExperienceResultService(sessionRepository, stepRepository, stepResultRepository,
                actionRepository, resultRepository, clock);
        scenario = new Scenario(1L, "CAFE", "카페", "설명", "cafe.png", true);
        mission = new Mission(scenario, "아메리카노 주문하기", "설명", "EASY", "{}", true);
        setId(mission, 101L);
        visitor = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        sessionId = UUID.randomUUID();
        lenient().when(stepRepository.countByScenarioId(1L)).thenReturn(4L);
    }

    @Test
    void completedMissionSessionCreatesResult() {
        ExperienceSession session = givenSession(SessionType.REAL_MISSION, mission);
        when(stepResultRepository.findCompletedStepIds(sessionId)).thenReturn(List.of(11L, 12L, 13L, 14L));
        when(actionRepository.countStepsWithValidAction(sessionId)).thenReturn(4L);
        when(actionRepository.countInvalidActionsInSession(sessionId)).thenReturn(2L);
        when(actionRepository.countActionsInSessionByType(sessionId, ActionType.BACK)).thenReturn(1L);
        when(actionRepository.countActionsInSessionByType(sessionId, ActionType.AI_HELP)).thenReturn(1L);

        EndSessionResponse response = service.end(sessionId, visitor, new EndSessionRequest(SessionStatus.COMPLETED));

        assertThat(response.status()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(response.resultAvailable()).isTrue();
        assertThat(response.endedAt().toString()).isEqualTo("2026-10-06T14:33:10+09:00");
        assertThat(session.getStatus()).isEqualTo(SessionStatus.COMPLETED);

        ArgumentCaptor<ExperienceResult> captor = ArgumentCaptor.forClass(ExperienceResult.class);
        verify(resultRepository).save(captor.capture());
        ExperienceResult result = captor.getValue();
        assertThat(result.getSuccess()).isTrue();
        assertThat(result.getElapsedSeconds()).isEqualTo(130);
        assertThat(result.getErrorCount()).isEqualTo(2);
        assertThat(result.getBackCount()).isEqualTo(1);
        assertThat(result.getAiHelpCount()).isEqualTo(1);
    }

    @Test
    void missionSessionWithUnfinishedStepCannotComplete() {
        ExperienceSession session = givenSession(SessionType.PRACTICE_MISSION, mission);
        when(stepResultRepository.findCompletedStepIds(sessionId)).thenReturn(List.of(11L, 12L));

        assertThatThrownBy(() -> service.end(sessionId, visitor, new EndSessionRequest(SessionStatus.COMPLETED)))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(ErrorCode.SESSION_NOT_READY_TO_COMPLETE));
        assertThat(session.getStatus()).isEqualTo(SessionStatus.IN_PROGRESS);
        verify(resultRepository, never()).save(any());
    }

    @Test
    void freeSessionCompletesWithoutStepCheck() {
        givenSession(SessionType.REAL_FREE, null);

        EndSessionResponse response = service.end(sessionId, visitor, new EndSessionRequest(SessionStatus.COMPLETED));

        assertThat(response.resultAvailable()).isTrue();
        ArgumentCaptor<ExperienceResult> captor = ArgumentCaptor.forClass(ExperienceResult.class);
        verify(resultRepository).save(captor.capture());
        assertThat(captor.getValue().getSuccess()).isNull();
        assertThat(captor.getValue().getErrorCount()).isNull();
        verify(stepResultRepository, never()).findCompletedStepIds(any());
    }

    @Test
    void abandonedSessionDoesNotCreateResult() {
        ExperienceSession session = givenSession(SessionType.REAL_MISSION, mission);

        EndSessionResponse response = service.end(sessionId, visitor, new EndSessionRequest(SessionStatus.ABANDONED));

        assertThat(response.status()).isEqualTo(SessionStatus.ABANDONED);
        assertThat(response.resultAvailable()).isFalse();
        assertThat(session.getEndedAt()).isNotNull();
        verify(resultRepository, never()).save(any());
    }

    @Test
    void endedSessionCannotEndAgain() {
        ExperienceSession session = givenSession(SessionType.REAL_MISSION, mission);
        session.end(SessionStatus.ABANDONED, LocalDateTime.now());

        assertThatThrownBy(() -> service.end(sessionId, visitor, new EndSessionRequest(SessionStatus.COMPLETED)))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(ErrorCode.SESSION_ALREADY_ENDED));
    }

    @Test
    void otherVisitorCannotEndSession() {
        givenSession(SessionType.REAL_MISSION, mission);
        AnonymousVisitor other = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        assertThatThrownBy(() -> service.end(sessionId, other, new EndSessionRequest(SessionStatus.COMPLETED)))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(ErrorCode.FORBIDDEN_SESSION));
    }

    @Test
    void inProgressIsNotAllowedAsEndStatus() {
        assertThatThrownBy(() -> service.end(sessionId, visitor, new EndSessionRequest(SessionStatus.IN_PROGRESS)))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(ErrorCode.INVALID_REQUEST));
    }

    @Test
    void resultIncludesWrongSteps() throws Exception {
        ExperienceSession session = givenSession(SessionType.REAL_MISSION, mission);
        session.end(SessionStatus.COMPLETED, LocalDateTime.of(2026, 10, 6, 14, 33, 10));
        when(resultRepository.findById(sessionId)).thenReturn(Optional.of(
                new ExperienceResult(sessionId, true, 130, 2, 1, 1, LocalDateTime.now())));
        ScenarioStep optionStep = new ScenarioStep(scenario, "SELECT_OPTION", 2, "옵션 고르기", null);
        setId(optionStep, 12L);
        when(stepResultRepository.findWrongStepResults(sessionId)).thenReturn(List.of(
                new StepResult(session, optionStep, 5000, 2, 0, LocalDateTime.now())));

        SessionResultResponse response = service.getResult(sessionId, visitor);

        assertThat(response.scenarioTitle()).isEqualTo("카페");
        assertThat(response.missionTitle()).isEqualTo("아메리카노 주문하기");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.elapsedSeconds()).isEqualTo(130);
        assertThat(response.errorCount()).isEqualTo(2);
        assertThat(response.wrongSteps()).hasSize(1);
        assertThat(response.wrongSteps().get(0).stepId()).isEqualTo(12L);
        assertThat(response.wrongSteps().get(0).stepName()).isEqualTo("옵션 고르기");
        assertThat(response.wrongSteps().get(0).errorCount()).isEqualTo(2);
    }

    @Test
    void freeSessionResultHasNoWrongSteps() {
        givenSession(SessionType.REAL_FREE, null);
        when(resultRepository.findById(sessionId)).thenReturn(Optional.of(
                new ExperienceResult(sessionId, null, 60, null, 0, 0, LocalDateTime.now())));

        SessionResultResponse response = service.getResult(sessionId, visitor);

        assertThat(response.missionTitle()).isNull();
        assertThat(response.isSuccess()).isNull();
        assertThat(response.errorCount()).isNull();
        assertThat(response.wrongSteps()).isEmpty();
        verify(stepResultRepository, never()).findWrongStepResults(any());
    }

    @Test
    void resultNotFoundWhenSessionHasNoResult() {
        givenSession(SessionType.REAL_MISSION, mission);
        when(resultRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getResult(sessionId, visitor))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(ErrorCode.RESULT_NOT_FOUND));
    }

    @Test
    void otherVisitorCannotReadResult() {
        givenSession(SessionType.REAL_MISSION, mission);
        AnonymousVisitor other = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        assertThatThrownBy(() -> service.getResult(sessionId, other))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(ErrorCode.FORBIDDEN_SESSION));
    }

    private ExperienceSession givenSession(SessionType type, Mission sessionMission) {
        LocalDateTime startedAt = LocalDateTime.of(2026, 10, 6, 14, 31, 0);
        ExperienceSession session = new ExperienceSession(sessionId, scenario, sessionMission, visitor, type,
                startedAt);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        return session;
    }

    private static void setId(Object target, Long id) throws Exception {
        Field field = target.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(target, id);
    }
}

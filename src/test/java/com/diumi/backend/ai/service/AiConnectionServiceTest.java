package com.diumi.backend.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import tools.jackson.databind.ObjectMapper;
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
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AiConnectionServiceTest {
    private static final UUID SESSION_ID = UUID.fromString("a3d9e2c1-77b4-4f0e-9a51-0c2b8e6f1d24");

    @Mock ExperienceSessionRepository sessionRepository;
    @Mock ScenarioStepRepository stepRepository;
    @Mock MissionStepGuideRepository missionStepGuideRepository;
    @Mock AiGuideRuleRepository aiGuideRuleRepository;
    @Mock UserActionRepository actionRepository;
    @Mock OpenAiRealtimeClient realtimeClient;

    private AiConnectionService service;
    private AnonymousVisitor visitor;
    private Scenario scenario;
    private Mission mission;
    private ScenarioStep step;

    @BeforeEach
    void setUp() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-10-26T05:40:00Z"), TimeConfig.KST);
        service = new AiConnectionService(sessionRepository, stepRepository, missionStepGuideRepository,
                aiGuideRuleRepository, actionRepository, realtimeClient, new AiPromptFactory(),
                new AiMissionContextFactory(new ObjectMapper()), clock);
        visitor = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        scenario = new Scenario(1L, "CAFE", "카페 주문", "주문", "image.png", true);
        mission = new Mission(scenario, "아메리카노 주문", "아이스 아메리카노 한 잔 주문",
                "EASY", "{\"PAY\":{\"target_code\":\"PAY_CARD\"}}", true);
        setId(mission, 101L);
        step = new ScenarioStep(scenario, "PAY", 4, "결제하기", null);
        setId(step, 14L);
    }

    @Test
    void startsRealtimeConnectionAndRecordsAiHelp() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_MISSION)));
        when(stepRepository.findById(14L)).thenReturn(Optional.of(step));
        when(missionStepGuideRepository.findByMissionIdAndStepId(any(), any())).thenReturn(
                Optional.of(new MissionStepGuide(mission, step,
                        "카드를 카드 투입구에 넣어 주세요.", "CARD_READER")));
        when(aiGuideRuleRepository
                .findByScenarioTypeAndScreenCodeInAndActiveTrueOrderByPriorityAscIdAsc(
                        "CAFE", List.of("COMMON", "PAY")))
                .thenReturn(List.of(new AiGuideRule("CAFE", "PAY", "SELECT_CARD",
                        "카드, 카드로", "카드로 할래요", "카드 결제 버튼을 직접 눌러 주세요.",
                        10, true)));
        when(realtimeClient.createClientSecret(any(), any())).thenReturn(
                new ClientSecret("ek_test", 1792990800L, "sess_7f3a", "gpt-realtime"));

        StartAiConnectionResponse response = service.start(SESSION_ID, visitor,
                new StartAiConnectionRequest(14L, null));

        assertThat(response.clientSecret()).isEqualTo("ek_test");
        assertThat(response.model()).isEqualTo("gpt-realtime");
        assertThat(response.conversationId()).isEqualTo("sess_7f3a");
        assertThat(response.expiresAt().getOffset().getTotalSeconds()).isEqualTo(9 * 3600);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(realtimeClient).createClientSecret(any(), promptCaptor.capture());
        assertThat(promptCaptor.getValue())
                .contains("카드를 카드 투입구에 넣어 주세요.", "CARD_READER")
                .contains("미션 제목: 아메리카노 주문")
                .contains("현재 단계 목표(JSON): {\"target_code\":\"PAY_CARD\"}")
                .contains("카드 결제 버튼을 직접 눌러 주세요.")
                .contains("버튼을 대신 누를 수 없습니다")
                .doesNotContain("null");

        ArgumentCaptor<UserAction> actionCaptor = ArgumentCaptor.forClass(UserAction.class);
        verify(actionRepository).save(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getActionType()).isEqualTo(ActionType.AI_HELP);
        assertThat(actionCaptor.getValue().getOccurredAt())
                .isEqualTo(LocalDateTime.parse("2026-10-26T14:40:00"));
    }

    @Test
    void usesRequestedScreenRulesWithoutChangingTheProgressStep() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_MISSION)));
        when(stepRepository.findById(14L)).thenReturn(Optional.of(step));
        when(missionStepGuideRepository.findByMissionIdAndStepId(any(), any()))
                .thenReturn(Optional.empty());
        when(aiGuideRuleRepository
                .findByScenarioTypeAndScreenCodeInAndActiveTrueOrderByPriorityAscIdAsc(
                        "CAFE", List.of("COMMON", "SELECT_PAYMENT")))
                .thenReturn(List.of(new AiGuideRule("CAFE", "SELECT_PAYMENT", "SELECT_CARD",
                        "카드", "카드로 할래요", "카드 결제 버튼을 직접 눌러 주세요.",
                        10, true)));
        when(realtimeClient.createClientSecret(any(), any())).thenReturn(
                new ClientSecret("ek_test", 1792990800L, "sess_7f3a", "gpt-realtime"));

        service.start(SESSION_ID, visitor,
                new StartAiConnectionRequest(14L, "SELECT_PAYMENT"));

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(realtimeClient).createClientSecret(any(), promptCaptor.capture());
        assertThat(promptCaptor.getValue())
                .contains("현재 화면 코드: SELECT_PAYMENT")
                .contains("현재 단계: 결제하기")
                .contains("카드 결제 버튼을 직접 눌러 주세요.");
    }

    @Test
    void rejectsUnregisteredExplicitScreenCode() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_MISSION)));
        when(stepRepository.findById(14L)).thenReturn(Optional.of(step));
        when(missionStepGuideRepository.findByMissionIdAndStepId(any(), any()))
                .thenReturn(Optional.empty());
        when(aiGuideRuleRepository
                .findByScenarioTypeAndScreenCodeInAndActiveTrueOrderByPriorityAscIdAsc(
                        "CAFE", List.of("COMMON", "UNKNOWN_SCREEN")))
                .thenReturn(List.of(new AiGuideRule("CAFE", "COMMON", "ASK_CURRENT_TASK",
                        "뭐 해야", "지금 뭘 해야 해요?", "현재 단계 안내를 확인해 주세요.",
                        10, true)));

        assertError(ErrorCode.INVALID_REQUEST,
                () -> service.start(SESSION_ID, visitor,
                        new StartAiConnectionRequest(14L, "UNKNOWN_SCREEN")));
        verify(realtimeClient, never()).createClientSecret(any(), any());
        verify(actionRepository, never()).save(any());
    }

    @Test
    void practiceMissionCannotUseAiHelp() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.PRACTICE_MISSION)));

        assertError(ErrorCode.AI_NOT_ALLOWED,
                () -> service.start(SESSION_ID, visitor, new StartAiConnectionRequest(14L, null)));
        verify(realtimeClient, never()).createClientSecret(any(), any());
    }

    @Test
    void endedSessionCannotStartConnection() throws Exception {
        ExperienceSession session = session(SessionType.REAL_MISSION);
        setField(session, "status", SessionStatus.COMPLETED);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        assertError(ErrorCode.SESSION_ALREADY_ENDED,
                () -> service.start(SESSION_ID, visitor, new StartAiConnectionRequest(14L, null)));
    }

    @Test
    void mapsOpenAiFailureAndDoesNotRecordHelp() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_FREE)));
        when(stepRepository.findById(14L)).thenReturn(Optional.of(step));
        when(realtimeClient.createClientSecret(any(), any()))
                .thenThrow(new OpenAiRealtimeClientException("failed"));

        assertError(ErrorCode.AI_CONNECTION_FAILED,
                () -> service.start(SESSION_ID, visitor, new StartAiConnectionRequest(14L, null)));
        verify(actionRepository, never()).save(any());
    }

    private ExperienceSession session(SessionType type) {
        return new ExperienceSession(SESSION_ID, scenario, type == SessionType.REAL_FREE ? null : mission,
                visitor, type, LocalDateTime.parse("2026-10-26T14:30:00"));
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

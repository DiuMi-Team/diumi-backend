package com.diumi.backend.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diumi.backend.ai.dto.SaveAiMessagesRequest;
import com.diumi.backend.ai.dto.SaveAiMessagesResponse;
import com.diumi.backend.ai.entity.AiMessage;
import com.diumi.backend.ai.entity.AiMessageRole;
import com.diumi.backend.ai.repository.AiMessageRepository;
import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.entity.Mission;
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
class AiMessageServiceTest {
    private static final UUID SESSION_ID = UUID.fromString("a3d9e2c1-77b4-4f0e-9a51-0c2b8e6f1d24");

    @Mock ExperienceSessionRepository sessionRepository;
    @Mock ScenarioStepRepository stepRepository;
    @Mock AiMessageRepository messageRepository;

    private AiMessageService service;
    private AnonymousVisitor visitor;
    private Scenario scenario;
    private Mission mission;
    private ScenarioStep step;

    @BeforeEach
    void setUp() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-10-26T05:45:00Z"), TimeConfig.KST);
        service = new AiMessageService(sessionRepository, stepRepository, messageRepository, clock);
        visitor = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        scenario = new Scenario(1L, "CAFE", "카페 주문", "주문", "image.png", true);
        mission = new Mission(scenario, "아메리카노", "주문", "EASY", "{}", true);
        step = new ScenarioStep(scenario, "PAY", 4, "결제하기", null);
        setId(step, 14L);
    }

    @Test
    void savesQuestionAndAnswerAsTwoMessages() throws Exception {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_MISSION)));
        when(stepRepository.findById(14L)).thenReturn(Optional.of(step));
        when(messageRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> {
            List<AiMessage> messages = invocation.getArgument(0);
            setId(messages.get(0), 9001L);
            setId(messages.get(1), 9002L);
            return messages;
        });

        SaveAiMessagesResponse response = service.save(SESSION_ID, visitor,
                new SaveAiMessagesRequest(14L, "  카드는 어디에 넣어요?  ",
                        "  화면 아래쪽 투입구에 넣어 주세요.  ", 1840));

        assertThat(response.questionMessageId()).isEqualTo(9001L);
        assertThat(response.answerMessageId()).isEqualTo(9002L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AiMessage>> captor = ArgumentCaptor.forClass(List.class);
        verify(messageRepository).saveAllAndFlush(captor.capture());
        List<AiMessage> messages = captor.getValue();
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getRole()).isEqualTo(AiMessageRole.USER);
        assertThat(messages.get(0).getContent()).isEqualTo("카드는 어디에 넣어요?");
        assertThat(messages.get(0).getResponseTimeMs()).isNull();
        assertThat(messages.get(1).getRole()).isEqualTo(AiMessageRole.AI);
        assertThat(messages.get(1).getContent()).isEqualTo("화면 아래쪽 투입구에 넣어 주세요.");
        assertThat(messages.get(1).getResponseTimeMs()).isEqualTo(1840);
        assertThat(messages.get(0).getCreatedAt()).isEqualTo(LocalDateTime.parse("2026-10-26T14:45:00"));
        assertThat(messages.get(1).getCreatedAt()).isEqualTo(messages.get(0).getCreatedAt());
    }

    @Test
    void completedSessionCanStillSaveLastConversation() throws Exception {
        ExperienceSession session = session(SessionType.REAL_MISSION);
        setField(session, "status", SessionStatus.COMPLETED);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));
        when(stepRepository.findById(14L)).thenReturn(Optional.of(step));
        when(messageRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> {
            List<AiMessage> messages = invocation.getArgument(0);
            setId(messages.get(0), 9001L);
            setId(messages.get(1), 9002L);
            return messages;
        });

        SaveAiMessagesResponse response = service.save(SESSION_ID, visitor,
                new SaveAiMessagesRequest(14L, "질문", "답변", null));

        assertThat(response.questionMessageId()).isEqualTo(9001L);
        assertThat(response.answerMessageId()).isEqualTo(9002L);
    }

    @Test
    void practiceMissionCannotSaveAiMessages() {
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.PRACTICE_MISSION)));

        assertError(ErrorCode.AI_NOT_ALLOWED,
                () -> service.save(SESSION_ID, visitor,
                        new SaveAiMessagesRequest(14L, "질문", "답변", null)));
        verify(messageRepository, never()).saveAllAndFlush(anyList());
    }

    @Test
    void anotherVisitorCannotSaveMessages() {
        AnonymousVisitor owner = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        ExperienceSession session = new ExperienceSession(SESSION_ID, scenario, mission, owner,
                SessionType.REAL_MISSION, LocalDateTime.now());
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        assertError(ErrorCode.FORBIDDEN_SESSION,
                () -> service.save(SESSION_ID, visitor,
                        new SaveAiMessagesRequest(14L, "질문", "답변", null)));
    }

    @Test
    void rejectsStepFromDifferentScenario() throws Exception {
        Scenario otherScenario = new Scenario(2L, "TRAIN", "기차", "예매", "train.png", true);
        ScenarioStep otherStep = new ScenarioStep(otherScenario, "PAY", 4, "결제하기", null);
        setId(otherStep, 14L);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(session(SessionType.REAL_FREE)));
        when(stepRepository.findById(14L)).thenReturn(Optional.of(otherStep));

        assertError(ErrorCode.STEP_NOT_FOUND,
                () -> service.save(SESSION_ID, visitor,
                        new SaveAiMessagesRequest(14L, "질문", "답변", null)));
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

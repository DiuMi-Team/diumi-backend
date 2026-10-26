package com.diumi.backend.session.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.diumi.backend.common.config.TimeConfig;
import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.mission.repository.MissionRepository;
import com.diumi.backend.mission.repository.MissionStepGuideRepository;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioRepository;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import com.diumi.backend.session.dto.StartSessionRequest;
import com.diumi.backend.session.dto.StartSessionResponse;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.session.repository.ExperienceSessionRepository;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExperienceSessionServiceTest {
    @Mock ScenarioRepository scenarioRepository;
    @Mock ScenarioStepRepository scenarioStepRepository;
    @Mock MissionRepository missionRepository;
    @Mock MissionStepGuideRepository guideRepository;
    @Mock ExperienceSessionRepository sessionRepository;

    private ExperienceSessionService service;
    private Scenario scenario;
    private ScenarioStep firstStep;
    private AnonymousVisitor visitor;

    @BeforeEach
    void setUp() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-10-26T05:31:00Z"), TimeConfig.KST);
        service = new ExperienceSessionService(scenarioRepository, scenarioStepRepository,
                missionRepository, guideRepository, sessionRepository, clock);
        scenario = new Scenario(1L, "CAFE", "카페", "주문 연습", "image.png", true);
        firstStep = new ScenarioStep(scenario, "OPEN_MENU", 1, "메뉴 고르기", "메뉴를 골라 보세요.");
        setId(firstStep, 11L);
        visitor = new AnonymousVisitor(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(scenario));
        lenient().when(scenarioStepRepository.findFirstByScenarioIdOrderByStepOrderAsc(1L))
                .thenReturn(Optional.of(firstStep));
        lenient().when(scenarioStepRepository.countByScenarioId(1L)).thenReturn(4L);
        lenient().when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void practiceMissionUsesMissionGuide() throws Exception {
        Mission mission = new Mission(scenario, "아메리카노", "주문", "EASY", "{}", true);
        setId(mission, 101L);
        when(missionRepository.findById(101L)).thenReturn(Optional.of(mission));
        when(guideRepository.findByMissionIdAndStepId(101L, 11L)).thenReturn(Optional.of(
                new MissionStepGuide(mission, firstStep, "아메리카노를 골라 보세요.", "MENU_AMERICANO")));

        StartSessionResponse response = service.start(visitor,
                new StartSessionRequest(1L, SessionType.PRACTICE_MISSION, 101L));

        assertThat(response.status().name()).isEqualTo("IN_PROGRESS");
        assertThat(response.totalSteps()).isEqualTo(4);
        assertThat(response.aiHelpEnabled()).isFalse();
        assertThat(response.currentStep().guideMessage()).isEqualTo("아메리카노를 골라 보세요.");
        assertThat(response.currentStep().highlightTargetCode()).isEqualTo("MENU_AMERICANO");
        assertThat(response.startedAt().getOffset().getTotalSeconds()).isEqualTo(9 * 3600);
    }

    @Test
    void realFreeHidesGuideAndEnablesAiHelp() {
        StartSessionResponse response = service.start(visitor,
                new StartSessionRequest(1L, SessionType.REAL_FREE, null));

        assertThat(response.missionId()).isNull();
        assertThat(response.aiHelpEnabled()).isTrue();
        assertThat(response.currentStep().guideMessage()).isNull();
        assertThat(response.currentStep().highlightTargetCode()).isNull();
    }

    @Test
    void practiceMissionFallsBackToScenarioGuide() throws Exception {
        Mission mission = new Mission(scenario, "아메리카노", "주문", "EASY", "{}", true);
        setId(mission, 101L);
        when(missionRepository.findById(101L)).thenReturn(Optional.of(mission));
        when(guideRepository.findByMissionIdAndStepId(101L, 11L)).thenReturn(Optional.empty());

        StartSessionResponse response = service.start(visitor,
                new StartSessionRequest(1L, SessionType.PRACTICE_MISSION, 101L));

        assertThat(response.currentStep().guideMessage()).isEqualTo("메뉴를 골라 보세요.");
        assertThat(response.currentStep().highlightTargetCode()).isNull();
    }

    @Test
    void rejectsMissionFromDifferentScenario() throws Exception {
        Scenario otherScenario = new Scenario(2L, "TRAIN", "기차", "예매", "train.png", true);
        Mission mission = new Mission(otherScenario, "표 예매", "예매", "EASY", "{}", true);
        setId(mission, 101L);
        when(missionRepository.findById(101L)).thenReturn(Optional.of(mission));

        assertThatThrownBy(() -> service.start(visitor,
                new StartSessionRequest(1L, SessionType.REAL_MISSION, 101L)))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.MISSION_SCENARIO_MISMATCH));
    }

    @Test
    void rejectsMissionIdForRealFree() {
        assertThatThrownBy(() -> service.start(visitor,
                new StartSessionRequest(1L, SessionType.REAL_FREE, 101L)))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.INVALID_REQUEST));
    }

    private static void setId(Object target, Long id) throws Exception {
        Field field = target.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(target, id);
    }
}

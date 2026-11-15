package com.diumi.backend.ai.prompt;

import static org.assertj.core.api.Assertions.assertThat;

import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AiMissionContextFactoryTest {
    private final AiMissionContextFactory factory = new AiMissionContextFactory(new ObjectMapper());

    @Test
    void extractsCurrentStepTargetFromMissionOnEveryRequest() {
        Scenario scenario = scenario();
        Mission mission = new Mission(scenario, "아이스 아메리카노 주문",
                "아이스 아메리카노 한 잔을 카드로 결제하세요.", "EASY",
                "{\"SELECT_MENU\":{\"target_code\":\"MENU_AMERICANO\"},"
                        + "\"PAY\":{\"target_code\":\"PAY_CARD\"}}", true);
        ScenarioStep step = new ScenarioStep(scenario, "PAY", 4, "결제 방법 선택", null);

        String context = factory.create(session(scenario, mission, SessionType.REAL_MISSION), step);

        assertThat(context)
                .contains("미션 제목: 아이스 아메리카노 주문")
                .contains("현재 단계 목표(JSON): {\"target_code\":\"PAY_CARD\"}");
    }

    @Test
    void explainsThatFreeSessionHasNoMission() {
        Scenario scenario = scenario();
        ScenarioStep step = new ScenarioStep(scenario, "SELECT_MENU", 1, "메뉴 선택", null);

        assertThat(factory.create(session(scenario, null, SessionType.REAL_FREE), step))
                .isEqualTo("자유 체험이므로 정해진 미션 조건이 없습니다.");
    }

    private Scenario scenario() {
        return new Scenario(1L, "CAFE", "카페 주문", "주문", "image.png", true);
    }

    private ExperienceSession session(Scenario scenario, Mission mission, SessionType type) {
        AnonymousVisitor visitor = new AnonymousVisitor(
                UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        return new ExperienceSession(UUID.randomUUID(), scenario, mission, visitor, type,
                LocalDateTime.now());
    }
}

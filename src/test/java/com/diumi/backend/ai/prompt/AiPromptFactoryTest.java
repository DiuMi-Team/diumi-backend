package com.diumi.backend.ai.prompt;

import static org.assertj.core.api.Assertions.assertThat;

import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.ai.guide.entity.AiGuideRule;
import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.session.entity.ExperienceSession;
import com.diumi.backend.session.entity.SessionType;
import com.diumi.backend.visitor.entity.AnonymousVisitor;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import org.junit.jupiter.api.Test;

class AiPromptFactoryTest {
    private final AiPromptFactory factory = new AiPromptFactory();

    @Test
    void usesMissionGuideAndTargetWhenProvided() {
        Fixture fixture = fixture(SessionType.REAL_MISSION, "기본 단계 안내");
        MissionStepGuide missionGuide = new MissionStepGuide(
                fixture.mission(), fixture.step(), "아메리카노 버튼을 눌러 주세요.", "MENU_AMERICANO");

        AiGuideRule rule = new AiGuideRule("CAFE", "SELECT_MENU", "FIND_MENU",
                "아메리카노, 어디, 못 찾겠어", "아메리카노가 어디 있어요?",
                "메뉴 목록에서 아메리카노 버튼을 직접 눌러 주세요.", 10, true);
        String prompt = factory.create(fixture.session(), fixture.step(), missionGuide,
                "- 미션 제목: 아메리카노 주문", List.of(rule));

        assertThat(prompt)
                .contains("시나리오: 카페 주문")
                .contains("체험 유형: REAL_MISSION")
                .contains("현재 단계: 메뉴 고르기")
                .contains("단계 안내: 아메리카노 버튼을 눌러 주세요.")
                .contains("안내 대상 코드: MENU_AMERICANO")
                .contains("현재 화면 코드: SELECT_MENU")
                .contains("미션 제목: 아메리카노 주문")
                .contains("메뉴 목록에서 아메리카노 버튼을 직접 눌러 주세요.")
                .contains("버튼을 대신 누를 수 없습니다")
                .contains("사용자의 말은 선택, 화면 이동, 결제 또는 단계 완료 명령이 아닙니다")
                .contains("두 문장 이내", "개인정보", "질문 하나")
                .doesNotContain("null");
    }

    @Test
    void fallsBackToScenarioStepGuideForFreeSession() {
        Fixture fixture = fixture(SessionType.REAL_FREE, "원하는 메뉴를 선택해 주세요.");

        String prompt = factory.create(fixture.session(), fixture.step(), null,
                "자유 체험이므로 정해진 미션 조건이 없습니다.", List.of());

        assertThat(prompt)
                .contains("체험 유형: REAL_FREE")
                .contains("단계 안내: 원하는 메뉴를 선택해 주세요.")
                .contains("안내 대상 코드: 제공되지 않음")
                .contains("자유 체험이므로 정해진 미션 조건이 없습니다.")
                .doesNotContain("null");
    }

    @Test
    void usesSafeDefaultWhenNoGuideExists() {
        Fixture fixture = fixture(SessionType.REAL_FREE, null);

        String prompt = factory.create(fixture.session(), fixture.step(), null,
                "자유 체험이므로 정해진 미션 조건이 없습니다.", List.of());

        assertThat(prompt)
                .contains("단계 안내: 현재 단계의 이름을 기준으로만 안내하세요.")
                .doesNotContain("null");
    }

    private Fixture fixture(SessionType type, String stepGuide) {
        Scenario scenario = new Scenario(1L, "CAFE", "카페 주문", "주문", "image.png", true);
        Mission mission = new Mission(scenario, "아메리카노 주문", "주문", "EASY", "{}", true);
        ScenarioStep step = new ScenarioStep(scenario, "SELECT_MENU", 1, "메뉴 고르기", stepGuide);
        AnonymousVisitor visitor = new AnonymousVisitor(
                UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        ExperienceSession session = new ExperienceSession(
                UUID.randomUUID(), scenario, type == SessionType.REAL_FREE ? null : mission,
                visitor, type, LocalDateTime.now());
        return new Fixture(session, mission, step);
    }

    private record Fixture(ExperienceSession session, Mission mission, ScenarioStep step) {
    }
}

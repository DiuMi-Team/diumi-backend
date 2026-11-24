package com.diumi.backend.ai.prompt;

import static org.assertj.core.api.Assertions.assertThat;

import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.ai.guide.entity.AiGuideRule;
import com.diumi.backend.ai.screen.dto.AiScreenInfo;
import com.diumi.backend.ai.screen.entity.AiScreenButton;
import com.diumi.backend.ai.screen.entity.AiScreenContext;
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
        String prompt = factory.create(fixture.session(), fixture.step(), "SELECT_MENU", null, missionGuide,
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

        String prompt = factory.create(fixture.session(), fixture.step(), "SELECT_MENU", null, null,
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

        String prompt = factory.create(fixture.session(), fixture.step(), "SELECT_MENU", null, null,
                "자유 체험이므로 정해진 미션 조건이 없습니다.", List.of());

        assertThat(prompt)
                .contains("단계 안내: 현재 단계의 이름을 기준으로만 안내하세요.")
                .doesNotContain("null");
    }

    @Test
    void includesScreenNameDescriptionAndButtonsWhenScreenInfoExists() {
        Fixture fixture = fixture(SessionType.REAL_MISSION, "기본 단계 안내");
        MissionStepGuide missionGuide = new MissionStepGuide(
                fixture.mission(), fixture.step(), "아메리카노를 눌러 주세요.", "MENU_AMERICANO");

        String prompt = factory.create(fixture.session(), fixture.step(), "SELECT_MENU",
                cafeMenuScreen(), missionGuide, "- 미션 제목: 아메리카노 주문", List.of());

        assertThat(prompt)
                .contains("[현재 화면 구성]")
                .contains("화면 이름: 메뉴 고르기")
                .contains("화면 설명: 음료 메뉴가 사진과 이름으로 나열된 화면입니다.")
                .contains("아메리카노 [MENU_AMERICANO]: 커피 메뉴 버튼")
                .contains("카페라떼 [MENU_CAFE_LATTE]: 우유가 들어간 커피 메뉴 버튼")
                .contains("[현재 화면 구성]에 없는 버튼은 안내하지 마세요")
                .contains("안내 대상 코드와 같은 코드의 버튼")
                .doesNotContain("현재 화면의 구성 정보가 등록되지 않았습니다")
                .doesNotContain("null");
    }

    @Test
    void omitsCodeForButtonWithoutActionCode() {
        Fixture fixture = fixture(SessionType.REAL_FREE, "더 담을지 골라 주세요.");
        AiScreenContext screen = new AiScreenContext("CAFE", "ADD_MORE_MENU", "메뉴 더 담기",
                "더 담을지 고르는 화면입니다.", true);
        AiScreenInfo screenInfo = new AiScreenInfo(screen, List.of(
                new AiScreenButton(1104L, null, "메뉴 더 담기", "메뉴 화면으로 돌아가는 버튼", 1),
                new AiScreenButton(1104L, "BUTTON_GO_CART", "주문하러 가기", "장바구니로 가는 버튼", 2)));

        String prompt = factory.create(fixture.session(), fixture.step(), "ADD_MORE_MENU",
                screenInfo, null, "자유 체험이므로 정해진 미션 조건이 없습니다.", List.of());

        assertThat(prompt)
                .contains("  - 메뉴 더 담기: 메뉴 화면으로 돌아가는 버튼")
                .contains("  - 주문하러 가기 [BUTTON_GO_CART]: 장바구니로 가는 버튼")
                .doesNotContain("[]")
                .doesNotContain("null");
    }

    @Test
    void listsCommonButtonsSeparately() {
        Fixture fixture = fixture(SessionType.REAL_FREE, "원하는 메뉴를 선택해 주세요.");
        AiScreenContext screen = new AiScreenContext("CAFE", "SELECT_MENU", "메뉴 고르기",
                "음료 메뉴가 나열된 화면입니다.", true);
        AiScreenInfo screenInfo = new AiScreenInfo(screen,
                List.of(new AiScreenButton(1103L, "MENU_AMERICANO", "아메리카노", "3,200원 커피 메뉴", 1)),
                List.of(new AiScreenButton(1100L, null, "이전으로", "화면 맨 위 오른쪽", 2)));

        String prompt = factory.create(fixture.session(), fixture.step(), "SELECT_MENU",
                screenInfo, null, "자유 체험이므로 정해진 미션 조건이 없습니다.", List.of());

        assertThat(prompt)
                .contains("아메리카노 [MENU_AMERICANO]: 3,200원 커피 메뉴")
                .contains("대부분의 화면에 공통으로 있는 버튼")
                .contains("  - 이전으로: 화면 맨 위 오른쪽")
                .doesNotContain("null");
    }

    @Test
    void warnsNotToGuessWhenScreenInfoIsMissing() {
        Fixture fixture = fixture(SessionType.REAL_FREE, "원하는 메뉴를 선택해 주세요.");

        String prompt = factory.create(fixture.session(), fixture.step(), "SELECT_MENU", null, null,
                "자유 체험이므로 정해진 미션 조건이 없습니다.", List.of());

        assertThat(prompt)
                .contains("현재 화면의 구성 정보가 등록되지 않았습니다")
                .contains("버튼 이름과 위치를 추측하지 마세요")
                .doesNotContain("null");
    }

    private AiScreenInfo cafeMenuScreen() {
        AiScreenContext screen = new AiScreenContext("CAFE", "SELECT_MENU", "메뉴 고르기",
                "음료 메뉴가 사진과 이름으로 나열된 화면입니다.", true);
        return new AiScreenInfo(screen, List.of(
                new AiScreenButton(1102L, "MENU_AMERICANO", "아메리카노", "커피 메뉴 버튼", 1),
                new AiScreenButton(1102L, "MENU_CAFE_LATTE", "카페라떼", "우유가 들어간 커피 메뉴 버튼", 2)));
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

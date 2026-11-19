package com.diumi.backend.result.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.diumi.backend.action.entity.ActionType;
import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.result.dto.SessionResultResponse.ConditionResult;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class MissionConditionEvaluatorTest {
    private static final String TARGET_DATA = """
            {"conditions":[
              {"code":"MENU","label":"아이스 아메리카노 선택","source":"CART_ITEM","step_code":"CART","action_type":"NEXT",
               "menu":"MENU_AMERICANO","expected":{"temperature":"TEMP_ICE"},"quantity_unit":"잔"},
              {"code":"SIZE_QUANTITY","label":"Large 사이즈 · 수량 2잔","source":"CART_ITEM","step_code":"CART","action_type":"NEXT",
               "menu":"MENU_AMERICANO","expected":{"size":"SIZE_LARGE","quantity":2},"quantity_unit":"잔"},
              {"code":"ORDER_TYPE","label":"포장 선택","source":"ACTION","step_code":"SELECT_ORDER_TYPE","action_type":"SELECT",
               "expected_target_code":"ORDER_TAKE_OUT"},
              {"code":"PAYMENT","label":"카드 결제 완료","source":"ACTION","step_code":"PAYMENT","action_type":"PAY",
               "expected_target_code":"PAY_CARD"}]}
            """;

    private final JsonMapper mapper = JsonMapper.builder().build();
    private final MissionConditionEvaluator evaluator = new MissionConditionEvaluator(mapper);
    private final Scenario scenario = new Scenario(1L, "CAFE", "카페", "설명", "cafe.png", true);

    @Test
    void reportsEachConditionWithDifference() {
        List<UserAction> actions = List.of(
                action("SELECT_ORDER_TYPE", ActionType.SELECT, "ORDER_EAT_IN", null),
                action("SELECT_ORDER_TYPE", ActionType.SELECT, "ORDER_TAKE_OUT", null),
                action("CART", ActionType.NEXT, "BUTTON_ORDER", """
                        {"items":[{"menu":"MENU_AMERICANO","temperature":"TEMP_ICE","size":"SIZE_REGULAR","quantity":1}]}
                        """));

        List<ConditionResult> results = evaluator.evaluate(TARGET_DATA, actions);

        assertThat(results).extracting(ConditionResult::passed).containsExactly(true, false, true, false);
        assertThat(results.get(1).detail()).isEqualTo("Regular · 1잔을 골랐어요");
        assertThat(results.get(3).detail()).isEqualTo("선택하지 않았어요");
    }

    @Test
    void reportsMissingMenuAndWrongChoice() {
        List<UserAction> actions = List.of(
                action("SELECT_ORDER_TYPE", ActionType.SELECT, "ORDER_EAT_IN", null),
                action("CART", ActionType.NEXT, "BUTTON_ORDER", "{\"items\":[{\"menu\":\"MENU_CAFE_LATTE\"}]}"));

        List<ConditionResult> results = evaluator.evaluate(TARGET_DATA, actions);

        assertThat(results.get(0).detail()).isEqualTo("아메리카노를 담지 않았어요");
        assertThat(results.get(2).detail()).isEqualTo("매장 식사를 골랐어요");
    }

    @Test
    void returnsEmptyWhenMissionHasNoConditions() {
        assertThat(evaluator.evaluate("{\"SELECT_MENU\":{}}", List.of())).isEmpty();
    }

    private UserAction action(String stepCode, ActionType type, String targetCode, String actionData) {
        ScenarioStep step = new ScenarioStep(scenario, stepCode, 1, stepCode, "안내");
        return new UserAction(null, step, type, targetCode, true,
                actionData == null ? null : mapper.readTree(actionData), LocalDateTime.now());
    }
}

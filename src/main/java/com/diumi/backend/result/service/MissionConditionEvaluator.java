package com.diumi.backend.result.service;

import com.diumi.backend.action.entity.UserAction;
import com.diumi.backend.result.dto.SessionResultResponse.ConditionResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * 미션 target_data의 conditions(완료 조건)를 사용자 행동 기록과 비교해 조건별 결과를 만든다.
 *
 * 조건 종류(source)
 * - ACTION    : step_code 단계에서 마지막으로 한 action_type 행동의 target_code가 expected_target_code와 같은지 본다.
 *               예) 포장 선택, 카드 결제
 * - CART_ITEM : step_code 단계에서 마지막으로 보낸 action_data.items(장바구니) 안에서 menu가 같은 메뉴를 찾아
 *               expected의 값(온도, 사이즈, 수량, 세트, 사이드, 음료 등)이 모두 같은지 본다.
 */
@Component
public class MissionConditionEvaluator {
    private final ObjectMapper objectMapper;

    public MissionConditionEvaluator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ConditionResult> evaluate(String targetData, List<UserAction> actions) {
        JsonNode conditions = readConditions(targetData);
        if (conditions == null) {
            return List.of();
        }
        List<ConditionResult> results = new ArrayList<>();
        for (JsonNode condition : conditions) {
            results.add(evaluateOne(condition, actions));
        }
        return results;
    }

    private JsonNode readConditions(String targetData) {
        if (targetData == null) {
            return null;
        }
        try {
            JsonNode conditions = objectMapper.readTree(targetData).get("conditions");
            return conditions != null && conditions.isArray() && !conditions.isEmpty() ? conditions : null;
        } catch (JacksonException exception) {
            return null;
        }
    }

    private ConditionResult evaluateOne(JsonNode condition, List<UserAction> actions) {
        String code = text(condition, "code");
        String label = text(condition, "label");
        UserAction last = findLastAction(actions, text(condition, "step_code"), text(condition, "action_type"));
        if ("CART_ITEM".equals(text(condition, "source"))) {
            return evaluateCartItem(code, label, condition, last);
        }
        return evaluateAction(code, label, condition, last);
    }

    private ConditionResult evaluateAction(String code, String label, JsonNode condition, UserAction last) {
        if (last == null || last.getTargetCode() == null) {
            return new ConditionResult(code, label, false, "선택하지 않았어요");
        }
        String expected = text(condition, "expected_target_code");
        if (last.getTargetCode().equals(expected)) {
            return new ConditionResult(code, label, true, null);
        }
        String actual = KioskCodeLabels.label(last.getTargetCode());
        return new ConditionResult(code, label, false, KioskCodeLabels.withObjectParticle(actual) + " 골랐어요");
    }

    private ConditionResult evaluateCartItem(String code, String label, JsonNode condition, UserAction last) {
        String menu = text(condition, "menu");
        JsonNode items = last == null || last.getActionData() == null ? null : last.getActionData().get("items");
        JsonNode item = findItem(items, menu);
        if (item == null) {
            return new ConditionResult(code, label, false,
                    KioskCodeLabels.withObjectParticle(KioskCodeLabels.label(menu)) + " 담지 않았어요");
        }

        JsonNode expected = condition.get("expected");
        List<String> differences = new ArrayList<>();
        if (expected != null && expected.isObject()) {
            for (Map.Entry<String, JsonNode> field : expected.properties()) {
                JsonNode actual = item.get(field.getKey());
                if (actual == null || actual.isNull()) {
                    differences.add(fieldName(field.getKey()) + " 선택 안 함");
                } else if (!actual.equals(field.getValue())) {
                    differences.add(describe(field.getKey(), actual, text(condition, "quantity_unit")));
                }
            }
        }
        if (differences.isEmpty()) {
            return new ConditionResult(code, label, true, null);
        }
        return new ConditionResult(code, label, false,
                KioskCodeLabels.withObjectParticle(String.join(" · ", differences)) + " 골랐어요");
    }

    private JsonNode findItem(JsonNode items, String menu) {
        if (items == null || !items.isArray()) {
            return null;
        }
        for (JsonNode item : items) {
            JsonNode itemMenu = item.get("menu");
            if (itemMenu != null && itemMenu.isString() && itemMenu.asString().equals(menu)) {
                return item;
            }
        }
        return null;
    }

    private UserAction findLastAction(List<UserAction> actions, String stepCode, String actionType) {
        UserAction found = null;
        for (UserAction action : actions) {
            boolean sameStep = stepCode == null || stepCode.equals(action.getStep().getStepCode());
            boolean sameType = actionType == null || actionType.equals(action.getActionType().name());
            if (sameStep && sameType) {
                found = action;
            }
        }
        return found;
    }

    private String describe(String field, JsonNode actual, String quantityUnit) {
        if ("quantity".equals(field) && actual.isNumber()) {
            return actual.asInt() + (quantityUnit == null ? "개" : quantityUnit);
        }
        return actual.isString() ? KioskCodeLabels.label(actual.asString()) : actual.toString();
    }

    private String fieldName(String field) {
        return switch (field) {
            case "temperature" -> "온도";
            case "size" -> "사이즈";
            case "quantity" -> "수량";
            case "meal_type" -> "세트·단품";
            case "side" -> "사이드";
            case "drink" -> "음료";
            default -> field;
        };
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asString();
    }
}

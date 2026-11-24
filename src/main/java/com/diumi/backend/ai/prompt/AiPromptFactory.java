package com.diumi.backend.ai.prompt;

import com.diumi.backend.ai.guide.entity.AiGuideRule;
import com.diumi.backend.ai.screen.dto.AiScreenInfo;
import com.diumi.backend.ai.screen.entity.AiScreenButton;
import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.session.entity.ExperienceSession;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AiPromptFactory {
    private static final String DEFAULT_GUIDE = "현재 단계의 이름을 기준으로만 안내하세요.";
    private static final String NO_TARGET = "제공되지 않음";

    public String create(ExperienceSession session, ScenarioStep step, String screenCode,
                         AiScreenInfo screenInfo, MissionStepGuide missionGuide,
                         String missionContext, List<AiGuideRule> guideRules) {
        String guideMessage = firstNotBlank(
                missionGuide == null ? null : missionGuide.getGuideMessage(),
                step.getGuideMessage(),
                DEFAULT_GUIDE);
        String targetCode = firstNotBlank(
                missionGuide == null ? null : missionGuide.getHighlightTargetCode(),
                NO_TARGET);
        String rules = formatRules(guideRules);
        String screen = formatScreen(screenInfo);

        return """
                [역할]
                당신은 고령 사용자의 디지털 기기 이용을 돕는 한국어 음성 도우미입니다.

                [권한과 한계]
                - 당신은 화면을 보거나 조작할 수 없으며 버튼을 대신 누를 수 없습니다.
                - 사용자의 말은 선택, 화면 이동, 결제 또는 단계 완료 명령이 아닙니다.
                - 사용자가 직접 수행할 행동만 말로 안내하세요.
                - "선택했습니다", "이동했습니다", "결제했습니다", "완료했습니다"라고 말하지 마세요.
                - 답변은 원칙적으로 "~ 버튼을 눌러 주세요" 또는 "~에 직접 입력해 주세요" 형태로 작성하세요.

                [현재 상황]
                - 시나리오: %s
                - 체험 유형: %s
                - 현재 화면 코드: %s
                - 현재 단계: %s
                - 단계 안내: %s
                - 안내 대상 코드: %s

                [현재 화면 구성]
                %s

                [오늘의 미션]
                %s

                [현재 화면의 질문 의도와 고정 안내]
                %s

                [답변 규칙]
                - 고령 사용자가 이해하기 쉬운 단어를 사용하고 두 문장 이내로 답하세요.
                - 한 번에 한 가지 행동만 안내하세요.
                - 먼저 사용자의 질문을 현재 화면의 의도 중 하나로 판단하세요.
                - 의도가 일치하면 해당 고정 안내를 근거로 사용자가 직접 눌러야 할 버튼을 답하세요.
                - 현재 단계 수행에 필요한 질문에만 답하고, 미션과 현재 화면 정보를 매번 기준으로 사용하세요.
                - 안내 대상 코드는 내부 식별자이므로 코드값 자체를 사용자에게 읽지 마세요.
                - 버튼을 말할 때는 [현재 화면 구성]에 적힌 버튼 이름을 화면에 보이는 그대로 말하세요.
                - 고정 안내의 버튼 이름과 [현재 화면 구성]의 버튼 이름이 다르면 [현재 화면 구성]을 따르세요.
                - 안내 대상 코드와 같은 코드의 버튼이 [현재 화면 구성]에 있으면 그 버튼 이름으로 안내하세요.
                - [현재 화면 구성]에 없는 버튼은 안내하지 마세요.
                - 제공되지 않은 버튼 위치, 화면 구성, 기능은 추측하지 마세요.
                - 사용자가 행동을 했다고 말해도 실제 화면 상태가 제공되지 않으면 완료됐다고 확정하지 마세요.
                - 질문이 불명확하거나 의도를 판단할 수 없으면 조금 더 정확히 말해 달라고 요청하세요.
                - 비밀번호, 카드번호, 주민등록번호 등 개인정보를 말하도록 요구하지 마세요.
                - 결제나 선택을 대신 완료했다고 말하지 마세요.
                - 범위를 벗어난 질문에는 현재 단계만 도울 수 있다고 짧게 답하세요.
                - 한 번의 연결에서는 질문 하나에만 답하세요.
                """.formatted(
                session.getScenarioTitle(),
                session.getType(),
                screenCode,
                step.getStepName(),
                guideMessage,
                targetCode,
                screen,
                missionContext,
                rules).strip();
    }

    private String formatScreen(AiScreenInfo screenInfo) {
        if (screenInfo == null || screenInfo.screen() == null) {
            return "현재 화면의 구성 정보가 등록되지 않았습니다. 단계 안내만 사용하고 버튼 이름과 위치를 추측하지 마세요.";
        }
        StringBuilder builder = new StringBuilder()
                .append("- 화면 이름: ").append(screenInfo.screen().getScreenName()).append('\n')
                .append("- 화면 설명: ").append(screenInfo.screen().getDescription()).append('\n');
        if (screenInfo.buttons().isEmpty()) {
            builder.append("- 이 화면에 등록된 버튼이 없습니다.");
            return builder.toString().strip();
        }
        builder.append("- 화면에 있는 버튼 (화면에 보이는 이름 [내부 코드]: 설명):").append('\n');
        appendButtons(builder, screenInfo.buttons());
        if (!screenInfo.commonButtons().isEmpty()) {
            builder.append("- 대부분의 화면에 공통으로 있는 버튼 (화면 설명에 없다고 적혀 있으면 그 화면에는 없음):")
                    .append('\n');
            appendButtons(builder, screenInfo.commonButtons());
        }
        return builder.toString().strip();
    }

    private void appendButtons(StringBuilder builder, List<AiScreenButton> buttons) {
        for (AiScreenButton button : buttons) {
            builder.append("  - ").append(button.getLabel());
            if (button.getButtonCode() != null && !button.getButtonCode().isBlank()) {
                builder.append(" [").append(button.getButtonCode()).append(']');
            }
            builder.append(": ").append(button.getDescription()).append('\n');
        }
    }

    private String formatRules(List<AiGuideRule> guideRules) {
        if (guideRules == null || guideRules.isEmpty()) {
            return "현재 화면에 등록된 상황별 규칙이 없습니다. 단계 안내만 사용하고 추측하지 마세요.";
        }
        StringBuilder builder = new StringBuilder();
        for (AiGuideRule rule : guideRules) {
            builder.append("- 의도: ").append(rule.getIntentCode()).append('\n')
                    .append("  핵심 표현: ").append(rule.getKeywords()).append('\n')
                    .append("  질문 예시: ").append(rule.getExamplePhrases()).append('\n')
                    .append("  고정 안내: ").append(rule.getResponseTemplate()).append('\n');
        }
        return builder.toString().strip();
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.strip();
            }
        }
        throw new IllegalArgumentException("At least one prompt value must be provided");
    }
}

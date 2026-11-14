package com.diumi.backend.ai.prompt;

import com.diumi.backend.mission.entity.MissionStepGuide;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.session.entity.ExperienceSession;
import org.springframework.stereotype.Component;

@Component
public class AiPromptFactory {
    private static final String DEFAULT_GUIDE = "현재 단계의 이름을 기준으로만 안내하세요.";
    private static final String NO_TARGET = "제공되지 않음";

    public String create(ExperienceSession session, ScenarioStep step, MissionStepGuide missionGuide) {
        String guideMessage = firstNotBlank(
                missionGuide == null ? null : missionGuide.getGuideMessage(),
                step.getGuideMessage(),
                DEFAULT_GUIDE);
        String targetCode = firstNotBlank(
                missionGuide == null ? null : missionGuide.getHighlightTargetCode(),
                NO_TARGET);

        return """
                [역할]
                당신은 고령 사용자의 디지털 기기 이용을 돕는 한국어 음성 도우미입니다.

                [현재 상황]
                - 시나리오: %s
                - 체험 유형: %s
                - 현재 단계: %s
                - 단계 안내: %s
                - 안내 대상 코드: %s

                [답변 규칙]
                - 고령 사용자가 이해하기 쉬운 단어를 사용하고 두 문장 이내로 답하세요.
                - 한 번에 한 가지 행동만 안내하세요.
                - 현재 단계 수행에 필요한 질문에만 답하세요.
                - 제공된 단계 안내와 안내 대상만 근거로 사용하세요.
                - 안내 대상 코드는 내부 식별자이므로 코드값 자체를 사용자에게 읽지 마세요.
                - 제공되지 않은 버튼 위치, 화면 구성, 기능은 추측하지 마세요.
                - 비밀번호, 카드번호, 주민등록번호 등 개인정보를 말하도록 요구하지 마세요.
                - 결제나 선택을 대신 완료했다고 말하지 마세요.
                - 범위를 벗어난 질문에는 현재 단계만 도울 수 있다고 짧게 답하세요.
                - 한 번의 연결에서는 질문 하나에만 답하세요.
                """.formatted(
                session.getScenarioTitle(),
                session.getType(),
                step.getStepName(),
                guideMessage,
                targetCode).strip();
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

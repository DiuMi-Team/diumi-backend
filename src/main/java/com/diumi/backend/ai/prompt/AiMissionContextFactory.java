package com.diumi.backend.ai.prompt;

import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.session.entity.ExperienceSession;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class AiMissionContextFactory {
    private static final String NO_MISSION = "자유 체험이므로 정해진 미션 조건이 없습니다.";

    private final ObjectMapper objectMapper;

    public AiMissionContextFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String create(ExperienceSession session, ScenarioStep step) {
        Mission mission = session.getMission();
        if (mission == null) {
            return NO_MISSION;
        }

        try {
            JsonNode targetData = objectMapper.readTree(mission.getTargetData());
            JsonNode currentStepTarget = targetData.get(step.getStepCode());
            String currentTarget = currentStepTarget == null
                    ? "현재 단계에 지정된 목표 조건이 없습니다."
                    : objectMapper.writeValueAsString(currentStepTarget);
            return """
                    - 미션 제목: %s
                    - 미션 설명: %s
                    - 전체 미션 목표(JSON): %s
                    - 현재 단계 목표(JSON): %s
                    """.formatted(
                    mission.getTitle(), mission.getDescription(),
                    objectMapper.writeValueAsString(targetData), currentTarget).strip();
        } catch (JacksonException exception) {
            return """
                    - 미션 제목: %s
                    - 미션 설명: %s
                    - 미션 목표 데이터는 해석할 수 없으므로 추측하지 마세요.
                    """.formatted(mission.getTitle(), mission.getDescription()).strip();
        }
    }
}

package com.diumi.backend.ai.screen.service;

import com.diumi.backend.ai.screen.dto.AiScreenInfo;
import com.diumi.backend.ai.screen.entity.AiScreenButton;
import com.diumi.backend.ai.screen.entity.AiScreenContext;
import com.diumi.backend.ai.screen.repository.AiScreenButtonRepository;
import com.diumi.backend.ai.screen.repository.AiScreenContextRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiScreenInfoService {
    static final String COMMON_SCREEN = "COMMON";

    private final AiScreenContextRepository screenRepository;
    private final AiScreenButtonRepository buttonRepository;

    public AiScreenInfoService(AiScreenContextRepository screenRepository,
                               AiScreenButtonRepository buttonRepository) {
        this.screenRepository = screenRepository;
        this.buttonRepository = buttonRepository;
    }

    /**
     * 시나리오 종류와 화면 코드로 화면 정보를 찾는다. 등록되지 않은 화면이면 비어 있다.
     * 공통 버튼(COMMON)도 함께 담는다.
     */
    @Transactional(readOnly = true)
    public Optional<AiScreenInfo> find(String scenarioType, String screenCode) {
        if (COMMON_SCREEN.equals(screenCode)) {
            return Optional.empty();
        }
        return screenRepository.findByScenarioTypeAndScreenCodeAndActiveTrue(scenarioType, screenCode)
                .map(screen -> new AiScreenInfo(screen, buttonsOf(screen),
                        screenRepository
                                .findByScenarioTypeAndScreenCodeAndActiveTrue(scenarioType, COMMON_SCREEN)
                                .map(this::buttonsOf)
                                .orElse(List.of())));
    }

    private List<AiScreenButton> buttonsOf(AiScreenContext screen) {
        return buttonRepository.findByScreenIdOrderByDisplayOrderAscIdAsc(screen.getId());
    }
}

package com.diumi.backend.ai.screen.dto;

import com.diumi.backend.ai.screen.entity.AiScreenButton;
import com.diumi.backend.ai.screen.entity.AiScreenContext;
import java.util.List;

/**
 * 프롬프트에 넣을 화면 정보.
 * buttons는 현재 화면의 버튼, commonButtons는 모든 화면에 공통으로 있는 버튼(처음으로, 이전으로, 음성 도우미)이다.
 */
public record AiScreenInfo(AiScreenContext screen, List<AiScreenButton> buttons,
                           List<AiScreenButton> commonButtons) {
    public AiScreenInfo {
        buttons = buttons == null ? List.of() : List.copyOf(buttons);
        commonButtons = commonButtons == null ? List.of() : List.copyOf(commonButtons);
    }

    public AiScreenInfo(AiScreenContext screen, List<AiScreenButton> buttons) {
        this(screen, buttons, List.of());
    }
}

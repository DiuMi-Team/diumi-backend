package com.diumi.backend.scenario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.diumi.backend.scenario.dto.ScenarioListResponse;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.repository.ScenarioRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScenarioQueryServiceTest {
    @Mock ScenarioRepository scenarioRepository;

    private ScenarioQueryService service;

    @BeforeEach
    void setUp() {
        service = new ScenarioQueryService(scenarioRepository);
    }

    @Test
    void returnsActiveScenarios() {
        when(scenarioRepository.findAllByActiveTrueOrderByIdAsc()).thenReturn(List.of(
                new Scenario(1L, "CAFE", "카페", "카페 키오스크로 음료를 주문해 봐요", "cafe.png", true),
                new Scenario(2L, "FAST_FOOD", "패스트푸드", "햄버거 세트를 주문해 봐요", "fastfood.png", true)));

        ScenarioListResponse response = service.getActiveScenarios();

        assertThat(response.scenarios()).hasSize(2);
        ScenarioListResponse.ScenarioSummary first = response.scenarios().get(0);
        assertThat(first.scenarioId()).isEqualTo(1L);
        assertThat(first.scenarioType()).isEqualTo("CAFE");
        assertThat(first.title()).isEqualTo("카페");
        assertThat(first.thumbnailUrl()).isEqualTo("cafe.png");
    }

    @Test
    void returnsEmptyListWhenNoActiveScenario() {
        when(scenarioRepository.findAllByActiveTrueOrderByIdAsc()).thenReturn(List.of());

        assertThat(service.getActiveScenarios().scenarios()).isEmpty();
    }
}

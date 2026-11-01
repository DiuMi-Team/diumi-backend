package com.diumi.backend.scenario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.scenario.dto.ScenarioDetailResponse;
import com.diumi.backend.scenario.dto.ScenarioListResponse;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.entity.ScenarioStep;
import com.diumi.backend.scenario.repository.ScenarioRepository;
import com.diumi.backend.scenario.repository.ScenarioStepRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScenarioQueryServiceTest {
    @Mock ScenarioRepository scenarioRepository;
    @Mock ScenarioStepRepository stepRepository;

    private ScenarioQueryService service;

    @BeforeEach
    void setUp() {
        service = new ScenarioQueryService(scenarioRepository, stepRepository);
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

    @Test
    void detailReturnsStepsInOrder() throws Exception {
        Scenario scenario = new Scenario(1L, "CAFE", "카페", "설명", "cafe.png", true);
        ScenarioStep menu = new ScenarioStep(scenario, "SELECT_MENU", 1, "메뉴 고르기", null);
        ScenarioStep option = new ScenarioStep(scenario, "SELECT_OPTION", 2, "옵션 고르기", null);
        setId(menu, 11L);
        setId(option, 12L);
        when(scenarioRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(scenario));
        when(stepRepository.findAllByScenarioIdOrderByStepOrderAsc(1L)).thenReturn(List.of(menu, option));

        ScenarioDetailResponse response = service.getDetail(1L);

        assertThat(response.scenarioId()).isEqualTo(1L);
        assertThat(response.scenarioType()).isEqualTo("CAFE");
        assertThat(response.title()).isEqualTo("카페");
        assertThat(response.steps()).extracting(ScenarioDetailResponse.Step::stepId).containsExactly(11L, 12L);
        assertThat(response.steps().get(0).stepCode()).isEqualTo("SELECT_MENU");
    }

    @Test
    void detailRejectsMissingOrInactiveScenario() {
        when(scenarioRepository.findByIdAndActiveTrue(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDetail(9L))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.SCENARIO_NOT_FOUND));
    }

    private static void setId(Object target, Long id) throws Exception {
        Field field = target.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(target, id);
    }
}

package com.diumi.backend.mission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diumi.backend.common.exception.BusinessException;
import com.diumi.backend.common.exception.ErrorCode;
import com.diumi.backend.mission.dto.MissionListResponse;
import com.diumi.backend.mission.entity.Mission;
import com.diumi.backend.mission.entity.MissionDifficulty;
import com.diumi.backend.mission.repository.MissionRepository;
import com.diumi.backend.scenario.entity.Scenario;
import com.diumi.backend.scenario.repository.ScenarioRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MissionQueryServiceTest {
    @Mock ScenarioRepository scenarioRepository;
    @Mock MissionRepository missionRepository;

    private MissionQueryService service;
    private Scenario scenario;

    @BeforeEach
    void setUp() {
        service = new MissionQueryService(scenarioRepository, missionRepository);
        scenario = new Scenario(1L, "CAFE", "카페", "설명", "cafe.png", true);
    }

    @Test
    void returnsAllActiveMissionsWithoutDifficulty() throws Exception {
        Mission mission = new Mission(scenario, "아메리카노 주문하기", "① 아메리카노 1잔 담기", "EASY", "{}", true);
        setField(mission, "id", 101L);
        setField(mission, "estimatedMinutes", 3);
        when(scenarioRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(scenario));
        when(missionRepository.findAllByScenarioIdAndActiveTrueOrderByIdAsc(1L)).thenReturn(List.of(mission));

        MissionListResponse response = service.getMissions(1L, null);

        assertThat(response.missions()).hasSize(1);
        MissionListResponse.MissionSummary summary = response.missions().get(0);
        assertThat(summary.missionId()).isEqualTo(101L);
        assertThat(summary.title()).isEqualTo("아메리카노 주문하기");
        assertThat(summary.difficulty()).isEqualTo("EASY");
        assertThat(summary.estimatedMinutes()).isEqualTo(3);
    }

    @Test
    void filtersByDifficulty() {
        when(scenarioRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(scenario));
        when(missionRepository.findAllByScenarioIdAndDifficultyAndActiveTrueOrderByIdAsc(1L, "HARD"))
                .thenReturn(List.of());

        MissionListResponse response = service.getMissions(1L, MissionDifficulty.HARD);

        assertThat(response.missions()).isEmpty();
        verify(missionRepository, never()).findAllByScenarioIdAndActiveTrueOrderByIdAsc(anyLong());
    }

    @Test
    void rejectsMissingScenario() {
        when(scenarioRepository.findByIdAndActiveTrue(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMissions(9L, null))
                .isInstanceOfSatisfying(BusinessException.class,
                        exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.SCENARIO_NOT_FOUND));
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}

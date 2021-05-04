package nl.something.awesome.feature.toggle.service.controllers;

import static nl.something.awesome.feature.toggle.service.TestUtil.enableDTO;
import static nl.something.awesome.feature.toggle.service.TestUtil.percentageDTO;
import static nl.something.awesome.feature.toggle.service.TestUtil.planSwitchRequestDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import nl.something.awesome.feature.toggle.service.services.ToggleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ToggleControllerTest {

    private ToggleController toggleController;
    private List<FeatureToggle> fullToggleList;
    private FeatureToggle toggleOne;

    @Mock
    private ToggleService toggleService;

    @BeforeEach
    void setUp() {
        toggleController = new ToggleController(toggleService);
        fullToggleList = new ArrayList<>();
        toggleOne = new FeatureToggle("someName");
    }

    @Test
    void shouldRestrict() {
        toggleController.restrict(enableDTO, enableDTO.getToggleId());
        verify(toggleService).restrict(enableDTO);
    }

    @Test
    void shouldCallPlanSwitchWithDTO() {
        toggleController.planSwitch(planSwitchRequestDTO);
        verify(toggleService).planSwitch(planSwitchRequestDTO);
    }

    @Test
    void shouldDeleteToggle() {
        when(toggleService.deleteToggle(toggleOne.getId())).thenReturn(true);

        toggleController.deleteToggle(toggleOne.getId());
        verify(toggleService).deleteToggle(toggleOne.getId());
    }

    @Test
    void shouldGetFeatureListFromDatabase() {
        toggleController.getFullToggleListFromDatabase();
        verify(toggleService).getFullToggleListFromDatabase();
    }

    @Test
    void shouldGetToggleValue() {
        given(toggleService.getToggleValue("knownTestToggle")).willReturn(true);
        assertThat(toggleController.getToggleValue("knownTestToggle"), is(true));
    }

    @Test
    void shouldGetFullToggle() {
        given(toggleService.getFullToggle(anyString())).willReturn(new FeatureToggle(anyString()));
        assertThat(toggleController.getFullToggle("someName"), isA(FeatureToggle.class));
    }

    @Test
    void shouldGetFullToggleList() {
        given(toggleService.getFullToggleList()).willReturn(fullToggleList);
        assertThat(toggleController.getFullToggleList(), isA(List.class));
    }

    @Test
    void shouldToggleTheToggle() {
        toggleController.toggleToggle("unknownTestToggle");
        verify(toggleService).toggleToggle("unknownTestToggle");
    }

    @Test
    void shouldSetPercentage() {
        toggleController.enablePercentage(percentageDTO);
        verify(toggleService).enablePercentage(percentageDTO);
    }
}

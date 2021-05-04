package nl.something.awesome.feature.toggle.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import nl.something.awesome.feature.toggle.service.database.FeatureToggleDatabaseController;
import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import nl.something.awesome.feature.toggle.service.dto.EnableDTO;
import nl.something.awesome.feature.toggle.service.dto.FeatureToggleDTO;
import nl.something.awesome.feature.toggle.service.dto.PercentageDTO;
import nl.something.awesome.feature.toggle.service.services.ToggleService;
import org.exparity.hamcrest.date.LocalDateTimeMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ToggleServiceTest {

    private FeatureTogglesHashMap featureTogglesMap;
    private ToggleService toggleService;
    private FeatureToggle toggleOne;

    @Mock
    private FeatureToggleDatabaseController featureToggleDatabaseController;

    @BeforeEach
    void setUp() {
        toggleOne = new FeatureToggle("someName");
        featureTogglesMap = new FeatureTogglesHashMap();
        featureTogglesMap.getFeatureToggleHashMap().put(toggleOne.getId(), toggleOne);
        featureToggleDatabaseController.createFeatureToggle(toggleOne);
        toggleService = new ToggleService(featureTogglesMap, featureToggleDatabaseController);
    }

    @Test
    void shouldGetFullToggleList() {
        FeatureToggle toggleTwo = new FeatureToggle("someOtherName");
        featureTogglesMap.getFeatureToggleHashMap().put(toggleTwo.getId(), toggleTwo);

        List<FeatureToggle> list = toggleService.getFullToggleList();
        assertThat(list.size(), is(2));
    }

    @Test
    void shouldGetFullToggleListForDashboard() {
        FeatureToggle toggleTwo = new FeatureToggle("someOtherName");
        featureTogglesMap.getFeatureToggleHashMap().put(toggleTwo.getId(), toggleTwo);

        List<FeatureToggleDTO> list = toggleService.getFullToggleListForDashboard();
        assertThat(list.size(), is(2));
    }

    @SneakyThrows
    @Test
    void shouldProcessThePlannedSwitchValue() {
        FeatureToggle togglePlanSwitch = new FeatureToggle("somePlanSwitchId");
        featureTogglesMap.getFeatureToggleHashMap().put(togglePlanSwitch.getId(), togglePlanSwitch);
        assertFalse(toggleService.getToggleValue(togglePlanSwitch.getId()));

        assertNull(togglePlanSwitch.getPlannedSwitchDate());
        toggleService.planSwitch(TestUtil.planSwitchRequestDTO);
        assertNotNull(togglePlanSwitch.getPlannedSwitchDate());

        assertTrue(toggleService.getToggleValue(togglePlanSwitch.getId()));
    }

    @Test
    void shouldGetToggleValue(){
        assertFalse(toggleService.getToggleValue(toggleOne.getId()));

    }

    @Test
    void shouldGetToggleValueForDashboard(){
        assertFalse(toggleService.getToggleValue(toggleOne.getId(), true));
    }

    @Test
    void shouldDeleteFromHashMapAndDatabase() {
        assertFalse(featureTogglesMap.getFeatureToggleHashMap().isEmpty());

        toggleService.deleteToggle(toggleOne.getId());

        assertTrue(featureTogglesMap.getFeatureToggleHashMap().isEmpty());
        verify(featureToggleDatabaseController).deleteFeatureToggle(toggleOne.getId());
    }

    @Test
    void shouldGetFeatureTogglesFromDatabase() {
        toggleService.getFullToggleListFromDatabase();
        verify(featureToggleDatabaseController).getFeatureToggles();
    }

    @Test
    void shouldLoadFromDatabaseIfEmptyHashMap() {
        List<FeatureToggle> list = new ArrayList<>();
        list.add(toggleOne);
        when(featureToggleDatabaseController.getFeatureToggles()).thenReturn(list);

        featureTogglesMap.getFeatureToggleHashMap().clear();
        assertTrue(featureTogglesMap.getFeatureToggleHashMap().isEmpty());

        toggleService = new ToggleService(featureTogglesMap, featureToggleDatabaseController);

        verify(featureToggleDatabaseController).getFeatureToggles();
        assertEquals(1,featureTogglesMap.getFeatureToggleHashMap().size());
    }

    @Test
    void shouldGetToggleValueForExistingToggle() throws InterruptedException {
        FeatureToggle toggle = toggleService.getFullToggle("someName");
        assertThat(toggle.getNumberOfTimesRequested(), is(0L));

        TimeUnit.MILLISECONDS.sleep(5);
        LocalDateTime date = LocalDateTime.now();
        assertThat(toggle.getLastRequested(), LocalDateTimeMatchers.before(date));

        toggleService.toggleToggle("someName");

        assertThat(toggleService.getToggleValue("someName"), is(true));
        assertThat(toggle.getNumberOfTimesRequested(), is(1L));
        assertThat(toggle.getLastRequested(), LocalDateTimeMatchers.after(date));
    }

    @Test
    void shouldGetToggleValueForNewToggle() {
        assertThat(toggleService.getToggleValue("previouslyUnknownToggle"), is(false));
    }

    @Test
    void shouldGetFullToggle() {
        assertThat(toggleService.getFullToggle("someName"), is(toggleOne));
    }

    @Test
    void shouldToggleToggle() {
        toggleService.getFullToggle("toggleTest");
        assertThat(toggleService.getToggleValue("toggleTest"), is(false));
        toggleService.toggleToggle("toggleTest");
        assertThat(toggleService.getToggleValue("toggleTest"), is(true));
    }

    @Test
    void shouldRestrictToggle() {
        assertThat(toggleOne.getAllowedNumberOfTimes(), is(0));

        EnableDTO enableDTO = new EnableDTO();
        enableDTO.setToggleId(toggleOne.getId());
        enableDTO.setAllowedNumberOfTimes(5);

        toggleService.restrict(enableDTO);
        assertThat(toggleOne.getAllowedNumberOfTimes(), is(5));
    }

    @Test
    void shouldSetPercentage() {
        assertThat(toggleOne.getAllowPercentage(), is(100.0));

        PercentageDTO percentageDTO = new PercentageDTO();
        percentageDTO.setToggleId(toggleOne.getId());
        percentageDTO.setTogglePercentage(5);

        toggleService.enablePercentage(percentageDTO);
        assertThat(toggleOne.getAllowPercentage(), is(5.0));
    }
}

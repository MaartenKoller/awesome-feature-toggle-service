package nl.something.awesome.feature.toggle.service.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.something.awesome.feature.toggle.service.FeatureTogglesHashMap;
import nl.something.awesome.feature.toggle.service.database.FeatureToggleDatabaseController;
import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import nl.something.awesome.feature.toggle.service.dto.EnableDTO;
import nl.something.awesome.feature.toggle.service.dto.FeatureToggleDTO;
import nl.something.awesome.feature.toggle.service.dto.PercentageDTO;
import nl.something.awesome.feature.toggle.service.dto.PlanSwitchRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ToggleService {

    private final HashMap<String, FeatureToggle> featureToggleHashMap;
    private final FeatureToggleDatabaseController featureToggleDatabaseController;

    @Autowired
    public ToggleService(FeatureTogglesHashMap featureToggleHashMap,
        FeatureToggleDatabaseController featureToggleDatabaseController) {
        this.featureToggleHashMap = featureToggleHashMap.getFeatureToggleHashMap();
        this.featureToggleDatabaseController = featureToggleDatabaseController;
        if (this.featureToggleHashMap.isEmpty()) {
            log.info("Hashmap is empty, loading from database");
            loadFromDatabase();
        }
    }

    private void loadFromDatabase() {
        log.info("Starting: Loading Database into the HashMap");
        List<FeatureToggle> fullDBToggleList = getFullToggleListFromDatabase();
        int counter = 0;
        for (FeatureToggle featureToggle : fullDBToggleList) {
            counter++;
            log.info("adding: " + featureToggle.getId());
            log.info("adding FULL: " + featureToggle);
            featureToggleHashMap.put(featureToggle.getId(), featureToggle);
        }
        log.info("Added " + counter + " FeatureToggles to hashMap");
    }

    // TODO implement Cosmos DB change feed instead of this
    @Scheduled(fixedDelay = 10000)
    private void synchronizeHashMap() {
        log.info("Syncing database with hashMap");
        for (FeatureToggle featureToggle : getFullToggleListFromDatabase()) {
            featureToggleHashMap.put(featureToggle.getId(), featureToggle);
        }
    }


    public boolean getToggleValue(String toggleId) {
        checkOrCreate(toggleId);
        return featureToggleHashMap.get(toggleId).getValueForClient();
    }

    public boolean getToggleValue(String toggleId, boolean dashboard) {
        checkOrCreate(toggleId);
        return featureToggleHashMap.get(toggleId).getValueForDashboard();
    }

    public FeatureToggle getFullToggle(String toggleId) {
        checkOrCreate(toggleId);
        return featureToggleHashMap.get(toggleId);
    }

    public List<FeatureToggle> getFullToggleList() {
        List<FeatureToggle> fullToggleList = new ArrayList<>();
        for (String toggleId : featureToggleHashMap.keySet()) {
            FeatureToggle toggle = getFullToggle(toggleId);
            fullToggleList.add(toggle);
        }
        return fullToggleList;
    }

    public List<FeatureToggleDTO> getFullToggleListForDashboard() {
        List<FeatureToggleDTO> fullToggleList = new ArrayList<>();
        for (String toggleId : featureToggleHashMap.keySet()) {
            getToggleValue(toggleId, true); //
            fullToggleList.add(toggleToDtoMapper(getFullToggle(toggleId)));
        }
        return fullToggleList;
    }

    public List<FeatureToggle> getFullToggleListFromDatabase() {
        return featureToggleDatabaseController.getFeatureToggles();
    }

    public void toggleToggle(String toggleId) {
        checkOrCreate(toggleId);
        FeatureToggle featureToggle = featureToggleHashMap.get(toggleId);
        featureToggle.flipToggleValue();
        updateDatabase(featureToggle);
    }

    private void updateDatabase(FeatureToggle featureToggle) {
        featureToggleDatabaseController.updateFeatureToggle(featureToggle);
    }

    private void checkOrCreate(String toggleId) {
        if (featureToggleHashMap.get(toggleId) == null) {
            FeatureToggle newFeatureToggle = new FeatureToggle(toggleId);
            log.info("toggle did not previously exist, creating...");
            featureToggleHashMap.put(toggleId, newFeatureToggle);
            featureToggleDatabaseController.createFeatureToggle(newFeatureToggle);
        }
    }

    public void planSwitch(PlanSwitchRequestDTO planSwitchRequestDTO) {
        checkOrCreate(planSwitchRequestDTO.getToggleId());

        FeatureToggle featureToggle = featureToggleHashMap.get(planSwitchRequestDTO.getToggleId());
        LocalDateTime convertedPlannedSwitchDate = planSwitchRequestDTO.getPlannedSwitchDate();
        convertedPlannedSwitchDate = convertedPlannedSwitchDate.atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        featureToggle.setPlannedSwitchDate(convertedPlannedSwitchDate);
        featureToggle.setPlannedSwitchValue(planSwitchRequestDTO.isPlannedSwitchValue());
        updateDatabase(featureToggle);
    }

    public void restrict(EnableDTO enableDTO) {
        checkOrCreate(enableDTO.getToggleId());

        FeatureToggle featureToggle = featureToggleHashMap.get(enableDTO.getToggleId());
        log.info(String.format("feature toggle with id %s.",
            featureToggle.getId()));
        featureToggle.setAllowedNumberOfTimes(enableDTO.getAllowedNumberOfTimes());
        updateDatabase(featureToggle);
    }

    public void enablePercentage(PercentageDTO percentageDTO) {
        checkOrCreate(percentageDTO.getToggleId());

        FeatureToggle featureToggle = featureToggleHashMap.get(percentageDTO.getToggleId());
        log.info(String.format("Setting percentage on toggle with id %s.",
            featureToggle.getId()));
        featureToggle.setAllowPercentage(percentageDTO.getTogglePercentage());
        updateDatabase(featureToggle);

    }

    public boolean deleteToggle(String toggleId) {
        // important: use only one pipe because both need to be run
        return (deleteFromHashMap(toggleId) | deleteFromDatabase(toggleId));
    }

    private boolean deleteFromDatabase(String toggleId) {
        log.info("deleting from database: " + toggleId);
        return featureToggleDatabaseController.deleteFeatureToggle(toggleId);
    }

    private boolean deleteFromHashMap(String toggleId) {
        log.info("deleting from hashMap: " + toggleId);
        return featureToggleHashMap.remove(toggleId).toString().contains("id=" + toggleId);
    }

    private FeatureToggleDTO toggleToDtoMapper(FeatureToggle fullToggle) {
        return FeatureToggleDTO.builder()
            .id(fullToggle.getId())
            .isActive(fullToggle.isActive())
            .allowPercentage(fullToggle.getAllowPercentage())
            .numberOfTimesRequested(fullToggle.getNumberOfTimesRequested())
            .plannedSwitchDate(fullToggle.getPlannedSwitchDate())
            .plannedSwitchValue(fullToggle.isPlannedSwitchValue())
            .lastRequested(fullToggle.getLastRequested())
            .creationDate(fullToggle.getCreationDate())
            .allowedNumberOfTimes(fullToggle.getAllowedNumberOfTimes())
            .build();
    }


}

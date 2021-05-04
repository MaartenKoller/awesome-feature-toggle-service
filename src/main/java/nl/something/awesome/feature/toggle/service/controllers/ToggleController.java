package nl.something.awesome.feature.toggle.service.controllers;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import nl.something.awesome.feature.toggle.service.dto.EnableDTO;
import nl.something.awesome.feature.toggle.service.dto.PercentageDTO;
import nl.something.awesome.feature.toggle.service.dto.PlanSwitchRequestDTO;
import nl.something.awesome.feature.toggle.service.services.ToggleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
public class ToggleController {

    private final ToggleService toggleService;

    @Autowired
    public ToggleController(final ToggleService toggleService) {
        this.toggleService = toggleService;
    }

    @GetMapping("/toggles")
    public List<FeatureToggle> getFullToggleList() {
        log.info("Full toggle list retrieved");
        return toggleService.getFullToggleList();
    }

    @GetMapping("/toggles/{toggleId}/full")
    public FeatureToggle getFullToggle(@PathVariable final String toggleId) {
        log.info("Full toggle retrieved: " + toggleId);
        return toggleService.getFullToggle(toggleId);
    }

    @GetMapping("/toggles/{toggleId}/value")
    public boolean getToggleValue(@PathVariable final String toggleId) {
        log.info("toggle value retrieved: " + toggleId);
        return toggleService.getToggleValue(toggleId);
    }

    @GetMapping(value = "/toggles/{toggleId}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteToggle(@PathVariable final String toggleId) {
        log.info("deleting toggle: " + toggleId);
        return toggleService.deleteToggle(toggleId);
    }

    @GetMapping("/toggles/fromDB")
    public List<FeatureToggle> getFullToggleListFromDatabase() {
        log.info("Full toggle list from database retrieved");
        return toggleService.getFullToggleListFromDatabase();
    }

    @GetMapping("/toggles/{toggleId}/toggle")
    public void toggleToggle(@PathVariable final String toggleId) {
        log.info("Toggling toggle: " + toggleId);
        toggleService.toggleToggle(toggleId);
    }

    @PostMapping("/toggles/{toggleId}/planSwitch")
    public void planSwitch(@RequestBody PlanSwitchRequestDTO planSwitchRequestDTO) {
        toggleService.planSwitch(planSwitchRequestDTO);
    }

    @PostMapping("/toggles/{toggleId}/enable")
    public void restrict(@RequestBody EnableDTO enableDTO, @PathVariable final String toggleId) {
        log.info("Enabling toggle: " + toggleId + " for " + enableDTO.getAllowedNumberOfTimes()
            + " times");
        toggleService.restrict(enableDTO);
    }

    @PostMapping("/toggles/{toggleId}/percentage")
    public void enablePercentage(@RequestBody PercentageDTO percentageDTO) {
        log.info("Setting percentage for toggle: " + percentageDTO.getToggleId() + " at " + percentageDTO.getTogglePercentage()
            + "%");
        toggleService.enablePercentage(percentageDTO);
    }

}

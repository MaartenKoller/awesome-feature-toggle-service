package nl.something.awesome.feature.toggle.service.controllers;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.something.awesome.feature.toggle.service.dto.FeatureToggleDTO;
import nl.something.awesome.feature.toggle.service.services.ToggleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
public class DashboardController {

    private final ToggleService toggleService;

    @Autowired
    public DashboardController(final ToggleService toggleService) {
        this.toggleService = toggleService;
    }

    @GetMapping("/toggles/dashboard")
    public List<FeatureToggleDTO> getFullToggleListForDashboard() {
        log.info("Full toggle list retrieved for Dashboard");
        return toggleService.getFullToggleListForDashboard();
    }
}

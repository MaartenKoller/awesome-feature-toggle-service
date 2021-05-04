package nl.something.awesome.feature.toggle.service.controllers;

import static org.mockito.Mockito.verify;

import java.util.List;
import nl.something.awesome.feature.toggle.service.dto.FeatureToggleDTO;
import nl.something.awesome.feature.toggle.service.services.ToggleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    private DashboardController dashboardController;

    @Mock
    private ToggleService toggleService;

    @BeforeEach
    void setup() {
        dashboardController = new DashboardController(toggleService);
    }

    @Test
    void shouldReturnFeatureToggleDTOList() {
        List<FeatureToggleDTO> list = dashboardController.getFullToggleListForDashboard();
        verify(toggleService).getFullToggleListForDashboard();
    }

}

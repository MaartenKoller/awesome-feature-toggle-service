package nl.something.awesome.feature.toggle.service.database;

import static org.mockito.Mockito.verify;

import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeatureToggleDatabaseControllerTest {

    private FeatureToggleDatabaseController featureToggleController;
    private FeatureToggle featureToggle;
    @Mock
    private DocDbDao docDbDao;

    @BeforeEach
    void setUp() {
        featureToggleController = new FeatureToggleDatabaseController(docDbDao);
        featureToggle = new FeatureToggle("SomeId");
    }

    @Test
    void shouldPassFTToDocDbDaoOnCreateFT() {
        featureToggleController.createFeatureToggle(featureToggle);
        verify(docDbDao).createFeatureToggle(featureToggle);
    }

    @Test
    void shouldDeleteFeatureToggleIExist() {
        featureToggleController.deleteFeatureToggle(featureToggle.getId());
        verify(docDbDao).deleteFeatureToggle(featureToggle.getId());
    }

    @Test
    void shouldGetFeatureToggleById() {
        featureToggleController.getFeatureToggleById(featureToggle.getId());
        verify(docDbDao).readFeatureToggle(featureToggle.getId());
    }

    @Test
    void shouldGetListOfFeatureToggles() {
        featureToggleController.getFeatureToggles();
        verify(docDbDao).readFeatureToggles();
    }

    @Test
    void shouldUpdateFeatureToggle() {
        featureToggleController.updateFeatureToggle(featureToggle);
        verify(docDbDao).updateFeatureToggle(featureToggle);
    }
}

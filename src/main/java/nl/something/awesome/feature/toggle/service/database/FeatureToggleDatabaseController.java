package nl.something.awesome.feature.toggle.service.database;

import java.util.List;
import lombok.NonNull;
import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeatureToggleDatabaseController {

    private static FeatureToggleDatabaseController featureToggleController;

    private final DocDbDao docDbDao;

    @Autowired
    public FeatureToggleDatabaseController(DocDbDao docDbDao) {
        this.docDbDao = docDbDao;
    }

    public FeatureToggle createFeatureToggle(@NonNull FeatureToggle featureToggle) {
        return docDbDao.createFeatureToggle(featureToggle);
    }

    public boolean deleteFeatureToggle(@NonNull String id) {
        return docDbDao.deleteFeatureToggle(id);
    }

    public FeatureToggle getFeatureToggleById(@NonNull String id) {
        return docDbDao.readFeatureToggle(id);
    }

    public List<FeatureToggle> getFeatureToggles() {
        return docDbDao.readFeatureToggles();
    }

    public FeatureToggle updateFeatureToggle(@NonNull FeatureToggle featureToggle) {
        return docDbDao.updateFeatureToggle(featureToggle);
    }
}

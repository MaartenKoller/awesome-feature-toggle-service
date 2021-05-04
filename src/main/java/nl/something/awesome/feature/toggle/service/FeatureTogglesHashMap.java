package nl.something.awesome.feature.toggle.service;

import java.util.HashMap;
import lombok.Getter;
import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FeatureTogglesHashMap {
    private HashMap<String, FeatureToggle> featureToggleHashMap = new HashMap<>();
}

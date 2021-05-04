package nl.something.awesome.feature.toggle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Builder
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureToggleDTO {

    @NonNull
    private String id;
    private boolean isActive;
    private double allowPercentage;
    private Long numberOfTimesRequested;
    private LocalDateTime plannedSwitchDate;
    private boolean plannedSwitchValue;
    private LocalDateTime lastRequested;
    private LocalDateTime creationDate;
    private int allowedNumberOfTimes;
}

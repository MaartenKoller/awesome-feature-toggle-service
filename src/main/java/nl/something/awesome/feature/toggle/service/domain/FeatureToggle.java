package nl.something.awesome.feature.toggle.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureToggle {


    // name 'id' is mandatory - needed for Cosmos DB connection to replace generated id with name so to easily be able to identify FT's
    private final String id;

    @Setter(AccessLevel.NONE)
    private boolean isActive = false;

    @Setter(AccessLevel.NONE)
    private double allowPercentage = 100.0; // TODO

    @Setter(AccessLevel.NONE)
    private Long numberOfTimesRequested = 0L;
    private LocalDateTime plannedSwitchDate = null;
    private boolean plannedSwitchValue = false;

    @Setter(AccessLevel.NONE)
    private LocalDateTime lastRequested = LocalDateTime.now();
    private final LocalDateTime creationDate = LocalDateTime.now();
    private int allowedNumberOfTimes = 0;

    public boolean getValueForClient() {
        numberOfTimesRequested++;
        lastRequested = LocalDateTime.now();
        processPlannedSwitch();
        if (allowedNumberOfTimes > 0) {
            processRestriction();
            return true;
        }
        Random random = new Random();
        return random.nextDouble() <= allowPercentage / 100 ? isActive : !isActive;
        // can be simplified but isn't for slightly better readability
    }

    public boolean getValueForDashboard() {
        processPlannedSwitch();
        return isActive;
    }

    private void processRestriction() {
        if (allowedNumberOfTimes > 0) {
            log.info(id + " - has " + allowedNumberOfTimes + " more times left at true / 'active'");
            allowedNumberOfTimes--;
            if (allowedNumberOfTimes == 0) {
                log.info(id +
                    " - allowedNumberOfTimes is 0 (zero) --> restriction ends, back to actual value (which is: "
                    + isActive + ")");
            }
        }
    }

    private void processPlannedSwitch() {
        if (plannedSwitchDate != null && plannedSwitchDate.isBefore(LocalDateTime.now())) {
            isActive = plannedSwitchValue;
            log.info("plannedSwitchValue activated > resetting plannedSwitchDate & value");
            resetPlannedSwitchInfo();
        }
    }

    private void resetPlannedSwitchInfo() {
        plannedSwitchDate = null;
        plannedSwitchValue = false;
    }

    public void setAllowPercentage(double allowPercentage) {
        if (allowPercentage > 100.0) {
            allowPercentage = 100.0;
        }
        this.allowPercentage = allowPercentage;
    }

    public void flipToggleValue() {
        this.isActive = !this.isActive;
    }
}

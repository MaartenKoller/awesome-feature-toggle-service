package nl.something.awesome.feature.toggle.service.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PlanSwitchRequestDTO {

    @NonNull
    private String toggleId;
    @NonNull
    private boolean plannedSwitchValue;
    @NonNull
    private LocalDateTime plannedSwitchDate;
}

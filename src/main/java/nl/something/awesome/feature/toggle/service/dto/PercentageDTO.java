package nl.something.awesome.feature.toggle.service.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PercentageDTO {

    @NonNull
    private String toggleId;
    @NonNull
    private int togglePercentage;

}

package nl.something.awesome.feature.toggle.service.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class EnableDTO {

    @NonNull
    private String toggleId;
    @NonNull
    private int allowedNumberOfTimes;
}

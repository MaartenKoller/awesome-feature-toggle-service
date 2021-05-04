package nl.something.awesome.feature.toggle.service;

import java.time.LocalDateTime;
import nl.something.awesome.feature.toggle.service.dto.EnableDTO;
import nl.something.awesome.feature.toggle.service.dto.PercentageDTO;
import nl.something.awesome.feature.toggle.service.dto.PlanSwitchRequestDTO;

public class TestUtil {

    public static final PlanSwitchRequestDTO planSwitchRequestDTO = getPlanSwitchRequestDTO();
    public static final EnableDTO enableDTO = getEnableDTO();
    public static final PercentageDTO percentageDTO = getPercentageDTO();

    private static EnableDTO getEnableDTO() {
        EnableDTO enableDTO = new EnableDTO();
        enableDTO.setAllowedNumberOfTimes(3);
        enableDTO.setToggleId("someEnableId");

        return enableDTO;
    }

    private static PlanSwitchRequestDTO getPlanSwitchRequestDTO() {
        PlanSwitchRequestDTO planSwitchRequestDTO = new PlanSwitchRequestDTO();
        planSwitchRequestDTO.setToggleId("somePlanSwitchId");
        planSwitchRequestDTO.setPlannedSwitchValue(true);
        planSwitchRequestDTO.setPlannedSwitchDate(LocalDateTime.now()
            .minusHours(3)); // this will always work with or without daylight savings time

        return planSwitchRequestDTO;
    }

    private static PercentageDTO getPercentageDTO() {
        PercentageDTO percentageDTO = new PercentageDTO();
        percentageDTO.setTogglePercentage(5);
        percentageDTO.setToggleId("percentageToggleId");

        return percentageDTO;
    }
}

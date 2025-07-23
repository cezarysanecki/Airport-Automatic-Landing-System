package com.jakub.bone.domain.airport;

import lombok.Getter;
import com.jakub.bone.config.Constant;

@Getter
public class Airport {
    public static Runway runway2;
    public static Runway runway1;

    public Airport() {
        this.runway1 = Runway.builder()
                .id("R-1")
                .landingPoint(Constant.LANDING_POINT_RUNWAY_1)
                .corridor(new Corridor("C-1", Constant.Corridor.ENTRY_POINT_CORRIDOR_1, Constant.Corridor.FINAL_APPROACH_CORRIDOR_1))
                .available(true)
                .build();
        this.runway2 = Runway.builder()
                .id("R-2")
                .landingPoint(Constant.LANDING_POINT_RUNWAY_2)
                .corridor(new Corridor("C-2", Constant.Corridor.ENTRY_POINT_CORRIDOR_2, Constant.Corridor.FINAL_APPROACH_CORRIDOR_2))
                .available(true)
                .build();
    }
}

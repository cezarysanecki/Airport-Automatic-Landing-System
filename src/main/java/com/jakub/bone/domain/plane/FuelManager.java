package com.jakub.bone.domain.plane;

import lombok.Getter;
import lombok.Setter;

import static com.jakub.bone.config.Constant.CONSUMPTION_PER_SECOND;
import static com.jakub.bone.config.Constant.INITIAL_FUEL_LEVEL;

@Getter
@Setter
public class FuelManager {

    private double fuelLevel;

    public FuelManager() {
        this.fuelLevel = INITIAL_FUEL_LEVEL;
    }

    public void burnFuel() {
        fuelLevel -= CONSUMPTION_PER_SECOND;
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}

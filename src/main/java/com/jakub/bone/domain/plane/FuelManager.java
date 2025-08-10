package com.jakub.bone.domain.plane;

import lombok.Getter;

import static com.jakub.bone.config.Constant.CONSUMPTION_PER_SECOND;
import static com.jakub.bone.config.Constant.INITIAL_FUEL_LEVEL;

public class FuelManager {

    @Getter
    private double fuelLevel;

    private FuelManager(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public static FuelManager initialFuelLevel() {
        return new FuelManager(INITIAL_FUEL_LEVEL);
    }

    public FuelManager copy() {
        return new FuelManager(fuelLevel);
    }

    public void burnFuel() {
        fuelLevel -= CONSUMPTION_PER_SECOND;
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}

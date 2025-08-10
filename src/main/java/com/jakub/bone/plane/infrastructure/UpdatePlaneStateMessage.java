package com.jakub.bone.plane.infrastructure;

import com.jakub.bone.domain.Coordinates;

public class UpdatePlaneStateMessage {

    public Coordinates coordinates;
    public Double fuelLevel;

    public UpdatePlaneStateMessage() {
    }

    public UpdatePlaneStateMessage(Coordinates coordinates, Double fuelLevel) {
        this.coordinates = coordinates;
        this.fuelLevel = fuelLevel;
    }

}

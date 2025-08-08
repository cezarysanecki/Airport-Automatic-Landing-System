package com.jakub.bone.plane.message.structures;

import com.jakub.bone.shared.Coordinates;

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

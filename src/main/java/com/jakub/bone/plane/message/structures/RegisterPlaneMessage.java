package com.jakub.bone.plane.message.structures;

import com.jakub.bone.domain.plane.FlightPhase;
import com.jakub.bone.shared.Coordinates;

public class RegisterPlaneMessage {

    public String flightNumber;
    public boolean landed;
    public boolean isDestroyed;
    public FlightPhase phase;
    public Coordinates landingPoint;
    public Coordinates currentCoordinates;

    public RegisterPlaneMessage() {
    }

    public RegisterPlaneMessage(String flightNumber, boolean landed, boolean isDestroyed, FlightPhase phase, Coordinates landingPoint, Coordinates currentCoordinates) {
        this.flightNumber = flightNumber;
        this.landed = landed;
        this.isDestroyed = isDestroyed;
        this.phase = phase;
        this.landingPoint = landingPoint;
        this.currentCoordinates = currentCoordinates;
    }
}

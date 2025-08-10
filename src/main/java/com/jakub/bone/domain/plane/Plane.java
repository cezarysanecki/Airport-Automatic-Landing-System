package com.jakub.bone.domain.plane;

import com.jakub.bone.shared.Coordinates;
import lombok.Getter;

import java.io.Serializable;

import static com.jakub.bone.domain.plane.FlightPhase.DESCENDING;

@Getter
public class Plane implements Serializable {

    private String flightNumber;
    private boolean landed;
    private boolean isDestroyed;
    private FlightPhase phase;
    private Coordinates landingPoint;
    private FuelManager fuelManager;
    private Waypoints waypoints;
    private Coordinates currentCoordinates;

    private Plane(String flightNumber, FlightPhase flightPhase, FuelManager fuelManager, Waypoints waypoints) {
        this.flightNumber = flightNumber;
        this.phase = flightPhase;
        this.isDestroyed = false;
        this.landed = false;
        this.landingPoint = null;
        this.fuelManager = fuelManager;
        this.waypoints = waypoints;
        this.currentCoordinates = waypoints.next();
    }

    public static Plane createPlane(String flightNumber, FuelManager fuelManager, Waypoints waypoints) {
        return new Plane(
                flightNumber,
                DESCENDING,
                fuelManager,
                waypoints
        );
    }

    public Coordinates getCoordinates() {
        return currentCoordinates;
    }

}

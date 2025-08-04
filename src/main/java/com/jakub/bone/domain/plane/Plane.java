package com.jakub.bone.domain.plane;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.utils.WaypointGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

import static com.jakub.bone.config.Constant.HOLDING_ENTRY_ALTITUDE;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.DESCENDING;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.HOLDING;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.LANDING;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {

    public enum FlightPhase {
        DESCENDING, HOLDING, LANDING;
    }

    @Getter
    private String flightNumber;

    private boolean landed;
    private boolean isDestroyed;
    private FlightPhase phase;
    private Runway assignedRunway;
    private FuelManager fuelManager;
    private Waypoints waypoints;
    private Coordinates currentCoordinates;

    private Plane(String flightNumber, FlightPhase flightPhase, FuelManager fuelManager, Waypoints waypoints) {
        this.flightNumber = flightNumber;
        this.phase = flightPhase;
        this.isDestroyed = false;
        this.landed = false;
        this.assignedRunway = null;
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

    public boolean hasReached(Coordinates finalApproachPoint) {
        return currentCoordinates.equals(finalApproachPoint);
    }

    public void descend() {
        currentCoordinates = waypoints.next();
        fuelManager.burnFuel();

        if (waypoints.isLastWaypoint()) {
            changePhase(HOLDING);
            this.waypoints = Waypoints.atFirst(WaypointGenerator.prepareHoldingWaypoints());
        }
    }

    public void hold() {
        changePhase(HOLDING);
        currentCoordinates = waypoints.next();
        fuelManager.burnFuel();

        if (waypoints.isLastWaypoint()) {
            waypoints.resetToStart();
        }
    }

    public void land() {
        currentCoordinates = waypoints.next();
        fuelManager.burnFuel();

        if (waypoints.isLastWaypoint()) {
            this.currentCoordinates = assignedRunway.getLandingPoint();
            landed = true;
        }
    }

    public void setLandingPhase(List<Coordinates> landingWaypoints, Runway runway) {
        changePhase(LANDING);

        this.waypoints = Waypoints.atFirst(landingWaypoints);
        this.assignedRunway = runway;
    }

    public void changePhase(FlightPhase newPhase) {
        if (this.phase != newPhase) {
            log.info("Plane [{}]: transitioned to phase: {}", flightNumber, newPhase);
            this.phase = newPhase;
        }
    }

    public void destroyPlane() {
        this.isDestroyed = true;
    }

    public void setFuelLevel(double fuelLevel) {
        fuelManager.setFuelLevel(fuelLevel);
    }

    public void setCoordinates(Coordinates coordinates) {
        this.currentCoordinates = coordinates;
    }

    public boolean isOutOfFuel() {
        return fuelManager.isOutOfFuel();
    }

    public boolean isPlaneApproachingHoldingAltitude() {
        return currentCoordinates.getAltitude() == HOLDING_ENTRY_ALTITUDE;
    }

    public double getFuelLevel() {
        return fuelManager.getFuelLevel();
    }

    public Coordinates getCoordinates() {
        return currentCoordinates;
    }

}

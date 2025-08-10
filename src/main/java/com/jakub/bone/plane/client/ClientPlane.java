package com.jakub.bone.plane.client;

import com.jakub.bone.domain.plane.FlightPhase;
import com.jakub.bone.domain.plane.FuelManager;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.domain.plane.Waypoints;
import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.utils.WaypointGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

import static com.jakub.bone.domain.plane.FlightPhase.HOLDING;
import static com.jakub.bone.domain.plane.FlightPhase.LANDING;

@Log4j2
@Getter
@Setter
public class ClientPlane implements Serializable {

    @Getter
    private String flightNumber;

    private boolean landed;
    private boolean isDestroyed;
    private FlightPhase phase;
    private Coordinates landingPoint;
    private FuelManager fuelManager;
    private Waypoints waypoints;
    private Coordinates currentCoordinates;

    public ClientPlane(Plane plane) {
        this.flightNumber = plane.getFlightNumber();
        this.phase = plane.getPhase();
        this.isDestroyed = plane.isDestroyed();
        this.landed = plane.isLanded();
        this.landingPoint = plane.getLandingPoint();
        this.fuelManager = plane.getFuelManager();
        this.waypoints = plane.getWaypoints();
        this.currentCoordinates = plane.getCurrentCoordinates();
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
            currentCoordinates = landingPoint;
            landed = true;
        }
    }

    public void setLandingPhase(List<Coordinates> landingWaypoints, Coordinates landingPoint) {
        changePhase(LANDING);

        this.waypoints = Waypoints.atFirst(landingWaypoints);
        this.landingPoint = landingPoint;
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

    public void setCoordinates(Coordinates coordinates) {
        this.currentCoordinates = coordinates;
    }

    public boolean isOutOfFuel() {
        return fuelManager.isOutOfFuel();
    }

    public double getFuelLevel() {
        return fuelManager.getFuelLevel();
    }

    public Coordinates getCoordinates() {
        return currentCoordinates;
    }

}

package com.jakub.bone.domain.plane;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.utils.WaypointGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

import static com.jakub.bone.domain.plane.Plane.FlightPhase.DESCENDING;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.HOLDING;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.LANDING;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {

    public enum FlightPhase {
        DESCENDING, HOLDING, LANDING
    }

    @Getter
    private String flightNumber;
    private boolean landed;
    private boolean isDestroyed;
    private Navigator navigator;
    private FlightPhase phase;
    private Runway assignedRunway;
    private FuelManager fuelManager;

    private Plane(String flightNumber, FlightPhase flightPhase, Navigator navigator, FuelManager fuelManager) {
        this.flightNumber = flightNumber;
        this.phase = flightPhase;
        this.navigator = navigator;
        this.isDestroyed = false;
        this.landed = false;
        this.assignedRunway = null;
        this.fuelManager = fuelManager;
    }

    public static Plane createPlane(String flightNumber, Navigator navigator, FuelManager fuelManager) {
        return new Plane(
                flightNumber,
                DESCENDING,
                navigator,
                fuelManager
        );
    }

    public void descend() {
        navigator.move();
        fuelManager.burnFuel();

        if (navigator.isAtLastWaypoint()) {
            changePhase(HOLDING);
            navigator.assignNewWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
        }
    }

    public void hold() {
        changePhase(HOLDING);
        navigator.move();
        fuelManager.burnFuel();

        if (navigator.isAtLastWaypoint()) {
            navigator.setCurrentIndex(0);
        }
    }

    public void land(Runway runway, Coordinates landingPoint) {
        assignedRunway = runway;
        navigator.move();
        fuelManager.burnFuel();

        if (navigator.isAtLastWaypoint()) {
            navigator.setCoordinates(landingPoint);
            landed = true;
        }
    }

    public void setLandingPhase(List<Coordinates> landingWaypoints) {
        changePhase(LANDING);

        navigator.assignNewWaypoints(landingWaypoints);
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
        navigator.setCoordinates(coordinates);
    }

    public boolean isOutOfFuel() {
        return fuelManager.isOutOfFuel();
    }

    public double getFuelLevel() {
        return fuelManager.getFuelLevel();
    }

    public Coordinates getCoordinates() {
        return navigator.getCoordinates();
    }

    public List<Coordinates> getRiskZoneWaypoints() {
        return navigator.getRiskZoneWaypoints();
    }
}

package com.jakub.bone.domain.plane;

import com.jakub.bone.domain.airport.Coordinates;
import com.jakub.bone.domain.airport.Runway;
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

    private String flightNumber;
    private boolean landed;
    private List<Coordinates> waypoints;
    private boolean isDestroyed;
    private FuelManager fuelManager;
    private Navigator navigator;
    private FlightPhase phase;
    private Runway assignedRunway;

    public Plane(String flightNumber) {
        this.flightNumber = flightNumber;
        this.phase = DESCENDING;
        this.fuelManager = new FuelManager();
        this.navigator = new Navigator(fuelManager);
        this.isDestroyed = false;
        this.landed = false;
        this.assignedRunway = null;
    }

    public void descend() {
        navigator.move();
        if (navigator.isAtLastWaypoint()) {
            setPhase(HOLDING);
            navigator.setWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
            navigator.setCurrentIndex(0);
        }
    }

    public void hold() {
        setPhase(HOLDING);
        navigator.move();
        if (navigator.isAtLastWaypoint()) {
            navigator.setCurrentIndex(0);
        }
    }

    public void land(Runway runway) {
        assignedRunway = runway;
        navigator.move();
        if (navigator.isAtLastWaypoint()) {
            navigator.setCoordinates(runway.getLandingPoint());
            landed = true;
        }
    }

    public void setLandingPhase(Runway runway) {
        changePhase(LANDING);
        //setPhase(FlightPhase.LANDING);
        navigator.setWaypoints(WaypointGenerator.getLandingWaypoints(runway));
        navigator.setCurrentIndex(0);
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
}

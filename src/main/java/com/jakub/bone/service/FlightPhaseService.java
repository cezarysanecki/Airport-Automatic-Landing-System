package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Airport;
import com.jakub.bone.domain.airport.Coordinates;
import com.jakub.bone.domain.airport.Runway;
import lombok.extern.log4j.Log4j2;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utils.Messenger;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static com.jakub.bone.application.PlaneHandler.AirportInstruction.*;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.*;
import static com.jakub.bone.config.Constant.*;

@Log4j2
public class FlightPhaseService {
    private ControlTowerService controlTowerService;
    private Airport airport;
    private Messenger messenger;
    private Runway availableRunway;

    public FlightPhaseService(ControlTowerService controlTower, Airport airport, Messenger messenger) {
        this.controlTowerService = controlTower;
        this.airport = airport;
        this.messenger = messenger;
    }

    public void processFlightPhase(Plane plane, Coordinates coordinates, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        plane.getNavigator().setCoordinates(coordinates);
        switch (plane.getPhase()) {
            case DESCENDING -> handleDescent(plane, out);
            case HOLDING -> handleHolding(plane, out);
            case LANDING -> handleLanding(plane);
            default -> log.warn("Plane [{}]: unknown flight phase for {}", plane.getFlightNumber(), plane.getPhase());
        }
    }

    private void handleDescent(Plane plane, ObjectOutputStream out) throws IOException {
        if (controlTowerService.isPlaneApproachingHoldingAltitude(plane)) {
            enterHolding(plane, out);
        } else {
            applyDescending(plane, out);
        }
    }

    private void handleHolding(Plane plane, ObjectOutputStream out) throws IOException {
        Runway runway = getRunwayIfPlaneAtCorridor(plane);
        availableRunway = runway;

        if(runway != null && controlTowerService.isRunwayAvailable(runway)){
            applyLanding(plane, runway, out);
        } else {
            applyHolding(plane, out);
        }
    }

    private void handleLanding(Plane plane) throws IOException, ClassNotFoundException {
        if (availableRunway == null) {
            log.warn("Plane [{}]: cannot proceed with landing, no available runway", plane.getFlightNumber());
            return;
        }

        if (controlTowerService.hasLandedOnRunway(plane, availableRunway)) {
            plane.setLanded(true);

            waitForUpdate(LANDING_CHECK_DELAY);

            controlTowerService.removePlaneFromSpace(plane);
            log.info("Plane [{}]: successfully landed on runway [{}]", plane.getFlightNumber(), availableRunway.getId());
            return;
        }
        controlTowerService.releaseRunwayIfPlaneAtFinalApproach(plane, availableRunway);
    }
    private void enterHolding(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(DESCENT, out);
        plane.changePhase(HOLDING);
    }

    private void applyDescending(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(DESCENT, out);
        plane.changePhase(DESCENDING);
    }

    private void applyHolding(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(HOLD_PATTERN, out);
        plane.changePhase(HOLDING);
    }

    private void applyLanding(Plane plane, Runway runway, ObjectOutputStream out) throws IOException {
        controlTowerService.assignRunway(runway);
        plane.changePhase(LANDING);
        messenger.send(LAND, out);
        messenger.send(runway, out);
        log.info("Plane [{}]: instructed to {} on runway [{}]", plane.getFlightNumber(), LAND, runway.getId());
    }

    private Runway getRunwayIfPlaneAtCorridor(Plane plane) {
        Runway runway;
        if (plane.getNavigator().getCoordinates().equals(ENTRY_POINT_CORRIDOR_1)){
            return runway = Airport.runway1;
        }
        else if (plane.getNavigator().getCoordinates().equals(ENTRY_POINT_CORRIDOR_2)) {
            return runway = Airport.runway2;
        }
        return null;
    }

    private void waitForUpdate(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ex) {
            log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
    }
}

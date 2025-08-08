package com.jakub.bone.plane.server;

import com.jakub.bone.domain.airport.Airport;
import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.plane.message.Messenger;
import com.jakub.bone.plane.message.PlaneServerMessanger;
import com.jakub.bone.plane.message.structures.AssignRunwayMessage;
import com.jakub.bone.service.PlanesRadar;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static com.jakub.bone.config.Constant.Corridor.ENTRY_POINT_CORRIDOR_1;
import static com.jakub.bone.config.Constant.Corridor.ENTRY_POINT_CORRIDOR_2;
import static com.jakub.bone.config.Constant.LANDING_CHECK_DELAY;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.DESCENDING;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.HOLDING;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.LANDING;
import static com.jakub.bone.plane.server.PlaneHandlerServer.AirportInstruction.DESCENT;
import static com.jakub.bone.plane.server.PlaneHandlerServer.AirportInstruction.HOLD_PATTERN;
import static com.jakub.bone.plane.server.PlaneHandlerServer.AirportInstruction.LAND;

@Log4j2
public class PlanePhaseProcessorServer {

    private final PlanesRadar planesRadar;
    private Runway availableRunway;

    public PlanePhaseProcessorServer(PlanesRadar controlTower) {
        this.planesRadar = controlTower;
    }

    public void processFlightPhase(Plane plane, ObjectOutputStream out) throws IOException {
        switch (plane.getPhase()) {
            case DESCENDING -> handleDescent(plane, out);
            case HOLDING -> handleHolding(plane, out);
            case LANDING -> handleLanding(plane);
            default -> log.warn("Plane [{}]: unknown flight phase for {}", plane.getFlightNumber(), plane.getPhase());
        }
    }

    private void handleDescent(Plane plane, ObjectOutputStream out) throws IOException {
        if (plane.isPlaneApproachingHoldingAltitude()) {
            Messenger.send(out, DESCENT);
            plane.changePhase(HOLDING);
        } else {
            Messenger.send(out, DESCENT);
            plane.changePhase(DESCENDING);
        }
    }

    private void handleHolding(Plane plane, ObjectOutputStream out) throws IOException {
        Runway runway = getRunwayIfPlaneAtCorridor(plane);
        availableRunway = runway;

        if (runway != null && planesRadar.isRunwayAvailable(runway)) {
            planesRadar.assignRunway(runway);
            plane.changePhase(LANDING);
            Messenger.send(out, LAND);
            PlaneServerMessanger.send(out, new AssignRunwayMessage(runway));
            log.info("Plane [{}]: instructed to {} on runway [{}]", plane.getFlightNumber(), LAND, runway.getId());
        } else {
            Messenger.send(out, HOLD_PATTERN);
            plane.changePhase(HOLDING);
        }
    }

    private void handleLanding(Plane plane) {
        if (availableRunway == null) {
            log.warn("Plane [{}]: cannot proceed with landing, no available runway", plane.getFlightNumber());
            return;
        }

        if (plane.hasReached(availableRunway.getLandingPoint())) {
            plane.setLanded(true);

            waitForUpdate(LANDING_CHECK_DELAY);

            planesRadar.removePlaneFromSpace(plane.getFlightNumber());
            log.info("Plane [{}]: successfully landed on runway [{}]", plane.getFlightNumber(), availableRunway.getId());
            return;
        }
        if (plane.hasReached(availableRunway.getCorridor().getFinalApproachPoint())) {
            planesRadar.releaseRunway(availableRunway);
        }
    }

    private Runway getRunwayIfPlaneAtCorridor(Plane plane) {
        if (plane.hasReached(ENTRY_POINT_CORRIDOR_1)) {
            return Airport.runway1;
        } else if (plane.hasReached(ENTRY_POINT_CORRIDOR_2)) {
            return Airport.runway2;
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

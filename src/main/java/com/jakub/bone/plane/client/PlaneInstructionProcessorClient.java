package com.jakub.bone.plane.client;

import com.jakub.bone.plane.message.PlaneClientMessanger;
import com.jakub.bone.plane.message.structures.AssignRunwayMessage;
import com.jakub.bone.plane.server.PlaneHandlerServer;
import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.plane.message.Messenger;
import com.jakub.bone.utils.WaypointGenerator;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.jakub.bone.plane.server.PlaneHandlerServer.AirportInstruction.COLLISION;
import static com.jakub.bone.plane.server.PlaneHandlerServer.AirportInstruction.DESCENT;
import static com.jakub.bone.plane.server.PlaneHandlerServer.AirportInstruction.HOLD_PATTERN;
import static com.jakub.bone.plane.server.PlaneHandlerServer.AirportInstruction.LAND;

@Log4j2
@Getter
public class PlaneInstructionProcessorClient {

    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    private final Plane plane;
    private final PlaneStateSender planeStateSender;

    private boolean isProcessCompleted;
    private boolean descentLogged;
    private boolean holdPatternLogged;

    public PlaneInstructionProcessorClient(Plane plane, ObjectInputStream in, ObjectOutputStream out) {
        this.plane = plane;
        this.in = in;
        this.out = out;
        this.planeStateSender = new PlaneStateSender(out);
        this.descentLogged = false;
        this.holdPatternLogged = false;
    }

    public void processInstruction() throws IOException, ClassNotFoundException {
        PlaneHandlerServer.AirportInstruction airportInstruction = Messenger.handleResponseAirportInstruction(in);

        switch (airportInstruction) {
            case DESCENT -> handleDescent();
            case HOLD_PATTERN -> handleHoldPattern();
            case LAND -> handleLanding();
            case COLLISION -> handleCollision();
            case FULL -> abortProcess("No capacity in the airspace");
            case RISK_ZONE -> abortProcess("Initial location occupied");
            default -> log.warn("Plane [{}]: Unknown instruction: {}", plane.getFlightNumber(), airportInstruction);
        }
    }

    private void handleLanding() throws IOException, ClassNotFoundException {
        AssignRunwayMessage message = PlaneClientMessanger.handleAssignRunwayMessage(in);
        Runway runway = message.runway;

        plane.setLandingPhase(WaypointGenerator.prepareLandingWaypointsFor(runway), runway);

        log.info("Plane [{}]: instructed to {} on runway {{}]", plane.getFlightNumber(), LAND, runway.getId());
        while (!isProcessCompleted) {
            plane.land();

            planeStateSender.update(plane);
            if (plane.isOutOfFuel() || plane.getCoordinates() == null) {
                log.error("Plane [{}]: lost communication due to fuel or location issues", plane.getFlightNumber());
                break;
            }

            if (plane.isLanded()) {
                isProcessCompleted = true;
                log.info("Plane [{}]: successfully landed on runway {{}]", plane.getFlightNumber(), runway.getId());
            }
        }
    }

    private void handleDescent() {
        if (!descentLogged) {
            log.info("Plane [{}]: instructed to {}", plane.getFlightNumber(), DESCENT);
            descentLogged = true;
        }
        plane.descend();
    }

    private void handleHoldPattern() {
        if (!holdPatternLogged) {
            log.info("Plane [{}]: instructed to {}", plane.getFlightNumber(), HOLD_PATTERN);
            holdPatternLogged = true;
        }
        plane.hold();
    }

    private void abortProcess(String message) {
        log.info("Plane [{}]: {} Redirecting", plane.getFlightNumber(), message);
        isProcessCompleted = true;
    }

    private void handleCollision() {
        log.info("Plane [{}]: {} detected", plane.getFlightNumber(), COLLISION);
        plane.destroyPlane();
        isProcessCompleted = true;
    }
}

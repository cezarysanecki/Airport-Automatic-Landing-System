package com.jakub.bone.client;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.application.PlaneHandler;
import lombok.Getter;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utills.Messenger;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.jakub.bone.application.PlaneHandler.AirportInstruction.*;


@Log4j2
@Getter
public class PlaneInstructionHandler {
    private Plane plane;
    private Messenger messenger;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isProcessCompleted;
    private PlaneCommunicationService communicationService;
    private boolean descentLogged;
    private boolean holdPatternLogged;

    public PlaneInstructionHandler(Plane plane, Messenger messenger, ObjectInputStream in, ObjectOutputStream out) {
        this.plane = plane;
        this.messenger = messenger;
        this.in = in;
        this.out = out;
        this.communicationService = new PlaneCommunicationService(plane, messenger, out);
        this.descentLogged = false;
        this.holdPatternLogged = false;
    }

    public void processInstruction(PlaneHandler.AirportInstruction instruction) throws IOException, ClassNotFoundException {
        switch (instruction) {
            case DESCENT -> handleDescent();
            case HOLD_PATTERN -> handleHoldPattern();
            case LAND -> handleLanding();
            case COLLISION -> handleCollision();
            case FULL -> abortProcess("No capacity in the airspace");
            case RISK_ZONE -> abortProcess("Initial location occupied");
            default -> log.warn("Plane [{}]: Unknown instruction: {}", plane.getFlightNumber(), instruction);
        }
    }

    public void handleLanding() throws IOException, ClassNotFoundException {
        Runway runway = messenger.receiveAndParse(in, Runway.class);
        plane.setLandingPhase(runway);

        log.info("Plane [{}]: instructed to {} on runway {{}]", plane.getFlightNumber(), LAND, runway.getId());
        performLanding(runway);
    }

    private void performLanding(Runway runway) throws IOException {
        while (!isProcessCompleted) {
            if(!communicationService.sendFuelLevel()){
                return;
            }

            plane.land(runway);

            if(!communicationService.sendLocation()){
                return;
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

package com.jakub.bone.application;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.service.FlightPhaseService;
import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.utils.Messenger;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static com.jakub.bone.application.PlaneHandler.AirportInstruction.COLLISION;
import static com.jakub.bone.application.PlaneHandler.AirportInstruction.FULL;
import static com.jakub.bone.application.PlaneHandler.AirportInstruction.RISK_ZONE;
import static com.jakub.bone.config.Constant.AFTER_COLLISION_DELAY;
import static com.jakub.bone.config.Constant.UPDATE_DELAY;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.DESCENDING;

@Log4j2
public class PlaneHandler extends Thread {
    public enum AirportInstruction {
        DESCENT, HOLD_PATTERN, LAND, FULL, COLLISION, RISK_ZONE
    }

    private final Socket clientSocket;
    private final ControlTowerService controlTowerService;
    private final FlightPhaseService flightPhaseService;

    public PlaneHandler(
            ServerSocket serverSocket,
            ControlTowerService controlTowerService,
            FlightPhaseService flightPhaseService
    ) throws IOException {
        this.clientSocket = serverSocket.accept();
        this.controlTowerService = controlTowerService;
        this.flightPhaseService = flightPhaseService;
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");
        log.info("Server started: {}", clientSocket.toString());

        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            handleClient(in, out);
        } catch (EOFException | SocketException ex) {
            log.warn("Connection to client lost. Client disconnected: {}", ex.getMessage(), ex);
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage(), ex);
        } finally {
            closeResources(in, out);
            try {
                clientSocket.close();
            } catch (IOException ex) {
                log.error("Failed to close client socket: {}", ex.getMessage(), ex);
            }
        }
    }

    private void handleClient(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        Plane plane = Messenger.handleResponsePlane(in);

        if (!canRegisterPlane(out, plane.getFlightNumber())) {
            return;
        }

        try {
            Thread.sleep(UPDATE_DELAY);
        } catch (InterruptedException ex) {
            log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }

        if (controlTowerService.isAtCollisionRiskZone(plane)) {
            Messenger.send(out, RISK_ZONE);
            log.info("Plane [{}]: initial location occupied. Redirecting", plane.getFlightNumber());
            return;
        }

        controlTowerService.registerPlane(plane);

        log.info("Plane [{}]: registered at ({}, {}, {}) ", plane.getFlightNumber(), plane.getCoordinates().getX(), plane.getCoordinates().getY(), plane.getCoordinates().getAltitude());

        managePlane(plane, in, out);
    }

    private boolean canRegisterPlane(ObjectOutputStream out, String flightNumber) throws IOException {
        if (controlTowerService.isSpaceFull()) {
            Messenger.send(out, FULL);
            log.info("Plane [{}]: no capacity in airspace", flightNumber);
            return false;
        }
        return true;
    }

    private void managePlane(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        plane.setPhase(DESCENDING);

        while (true) {
            double fuelLevel = Messenger.handleResponseFuelLevel(in);
            plane.setFuelLevel(fuelLevel);

            if (fuelLevel <= 0) {
                handleOutOfFuel(plane);
                return;
            }

            Coordinates coordinates = Messenger.handleResponseCoordinates(in);
            flightPhaseService.processFlightPhase(plane, coordinates, out);

            if (plane.isDestroyed()) {
                handleCollision(plane, out);
                return;
            }

            if (plane.isLanded()) {
                log.info("Plane [{}]: successfully landed", plane.getFlightNumber());
                return;
            }
        }
    }

    private void handleCollision(Plane plane, ObjectOutputStream out) throws IOException {
        if (plane.getAssignedRunway() != null) {
            controlTowerService.releaseRunway(plane.getAssignedRunway());
        }
        controlTowerService.removePlaneFromSpace(plane.getFlightNumber());
        Messenger.send(out, COLLISION);

        try {
            Thread.sleep(AFTER_COLLISION_DELAY);
        } catch (InterruptedException ex) {
            log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
    }

    private void handleOutOfFuel(Plane plane) {
        plane.destroyPlane();
        controlTowerService.removePlaneFromSpace(plane.getFlightNumber());
        log.info("Plane [{}]: out of fuel. Disappeared from the radar", plane.getFlightNumber());
    }

    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ex) {
                    log.error("Failed to close resource: {}", ex.getMessage(), ex);
                }
            }
        }
    }
}



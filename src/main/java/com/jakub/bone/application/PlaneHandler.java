package com.jakub.bone.application;

import com.jakub.bone.domain.airport.Coordinates;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.service.FlightPhaseService;
import com.jakub.bone.utils.Messenger;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private final Messenger messenger;
    private final FlightPhaseService phaseCoordinator;

    public PlaneHandler(Socket clientSocket, ControlTowerService controlTowerService) {
        this.clientSocket = clientSocket;
        this.controlTowerService = controlTowerService;
        this.messenger = new Messenger();
        this.phaseCoordinator = new FlightPhaseService(controlTowerService, messenger);
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");
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
        Plane plane = messenger.receiveAndParse(in, Plane.class);

        if (!isPlaneRegistered(plane, out)) {
            return;
        }

        managePlane(plane, in, out);
    }

    private boolean isPlaneRegistered(Plane plane, ObjectOutputStream out) throws IOException {
        if (controlTowerService.isSpaceFull()) {
            messenger.send(FULL, out);
            log.info("Plane [{}]: no capacity in airspace", plane.getFlightNumber());
            return false;
        }

        waitForUpdate(UPDATE_DELAY);

        if (controlTowerService.isAtCollisionRiskZone(plane)) {
            messenger.send(RISK_ZONE, out);
            log.info("Plane [{}]: initial location occupied. Redirecting", plane.getFlightNumber());
            return false;
        }
        controlTowerService.registerPlane(plane);

        log.info("Plane [{}]: registered at ({}, {}, {}) ", plane.getFlightNumber(), plane.getNavigator().getCoordinates().getX(), plane.getNavigator().getCoordinates().getY(), plane.getNavigator().getCoordinates().getAltitude());
        return true;
    }

    private void managePlane(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        plane.setPhase(DESCENDING);

        while (true) {
            double fuelLevel = messenger.receiveAndParse(in, Double.class);
            plane.getFuelManager().setFuelLevel(fuelLevel);

            if (fuelLevel <= 0) {
                handleOutOfFuel(plane);
                return;
            }

            Coordinates coordinates = messenger.receiveAndParse(in, Coordinates.class);
            phaseCoordinator.processFlightPhase(plane, coordinates, out);

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
        controlTowerService.getPlanes().remove(plane);
        messenger.send(COLLISION, out);

        waitForUpdate(AFTER_COLLISION_DELAY);
    }

    private void handleOutOfFuel(Plane plane) throws IOException {
        plane.destroyPlane();
        controlTowerService.removePlaneFromSpace(plane);
        log.info("Plane [{}]: out of fuel. Disappeared from the radar", plane.getFlightNumber());
    }

    private void waitForUpdate(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ex) {
            log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
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



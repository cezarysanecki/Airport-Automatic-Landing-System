package com.jakub.bone.plane.server;

import com.jakub.bone.domain.plane.PlaneNumber;
import com.jakub.bone.plane.message.PlaneServerMessanger;
import com.jakub.bone.plane.message.structures.UpdatePlaneStateMessage;
import com.jakub.bone.service.PlanesRadar;
import com.jakub.bone.shared.Coordinates;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.jakub.bone.config.Constant.AFTER_COLLISION_DELAY;

@Log4j2
public class PlaneHandlerLoop {

    private final ServerPlane plane;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    private final PlanePhaseProcessorServer planePhaseProcessorServer;
    private final PlanesRadar planesRadar;

    public PlaneHandlerLoop(ServerPlane plane, ObjectInputStream in, ObjectOutputStream out, PlanePhaseProcessorServer planePhaseProcessorServer, PlanesRadar planesRadar) {
        this.plane = plane;
        this.in = in;
        this.out = out;
        this.planePhaseProcessorServer = planePhaseProcessorServer;
        this.planesRadar = planesRadar;
    }

    public void run() throws IOException, ClassNotFoundException {
        while (true) {
            UpdatePlaneStateMessage message = PlaneServerMessanger.handleUpdatePlaneStateMessage(in);
            Double fuelLevel = message.fuelLevel;
            Coordinates coordinates = message.coordinates;

            if (fuelLevel <= 0) {
                handleOutOfFuel(plane);
                return;
            }

            plane.setCoordinates(coordinates);
            planePhaseProcessorServer.processFlightPhase(plane, out);

            if (plane.isDestroyed()) {
                handleCollision(out);
                return;
            }

            if (plane.isLanded()) {
                log.info("Plane [{}]: successfully landed", plane.getFlightNumber());
                return;
            }
        }
    }

    private void handleCollision(ObjectOutputStream out) throws IOException {
        if (plane.getLandingPoint() != null) {
            planesRadar.releaseRunway(new PlaneNumber(plane.getFlightNumber()));
        }
        planesRadar.removePlaneFromSpace(new PlaneNumber(plane.getFlightNumber()));
        PlaneServerMessanger.sendCollisionCommand(out);

        try {
            Thread.sleep(AFTER_COLLISION_DELAY);
        } catch (InterruptedException ex) {
            log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
    }

    private void handleOutOfFuel(ServerPlane plane) {
        plane.destroyPlane();
        planesRadar.removePlaneFromSpace(new PlaneNumber(plane.getFlightNumber()));
        log.info("Plane [{}]: out of fuel. Disappeared from the radar", plane.getFlightNumber());
    }

}



package com.jakub.bone.service;

import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.shared.CollisionAreaDetector;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.util.List;

import static com.jakub.bone.config.Constant.COLLISION_CHECK_DELAY;

@Log4j2
public class CollisionService extends Thread {

    private final PlanesRadar planesRadar;
    private final CollisionRepository collisionRepository;

    public CollisionService(PlanesRadar planesRadar, CollisionRepository collisionRepository) {
        this.planesRadar = planesRadar;
        this.collisionRepository = collisionRepository;
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");
        while (true) {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    detectCollision();
                    Thread.sleep(COLLISION_CHECK_DELAY);
                }
            } catch (InterruptedException ex) {
                log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                break;
            }
        }
    }

    public void detectCollision() {
        List<ServerPlane> planes = planesRadar.getPlanes();

        for (ServerPlane plane : planes) {
            for (ServerPlane otherPlane : planes) {
                if (plane.equals(otherPlane)) {
                    continue;
                }

                if (CollisionAreaDetector.areClose(plane.getCoordinates(), otherPlane.getCoordinates())) {
                    handleCollision(plane, otherPlane);
                }
            }
        }
    }

    private void handleCollision(ServerPlane plane1, ServerPlane plane2) {
        String[] collidedIDs = {plane1.getFlightNumber().value(), plane2.getFlightNumber().value()};
        collisionRepository.registerCollisionToDB(collidedIDs);
        plane1.plane.destroyPlane();
        plane2.plane.destroyPlane();
        log.info("Collision detected between Plane [{}] and Plane [{}]", plane1.getFlightNumber(), plane2.getFlightNumber());
    }

}

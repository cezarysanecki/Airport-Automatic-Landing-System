package com.jakub.bone.service;

import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.repository.CollisionRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import static com.jakub.bone.config.Constant.ALTITUDE_COLLISION_DISTANCE;
import static com.jakub.bone.config.Constant.COLLISION_CHECK_DELAY;
import static com.jakub.bone.config.Constant.HORIZONTAL_COLLISION_DISTANCE;

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
        for (int i = 0; i < planesRadar.getPlanes().size(); i++) {
            Plane plane1 = planesRadar.getPlanes().get(i);
            for (int j = i + 1; j < planesRadar.getPlanes().size(); j++) {
                Plane plane2 = planesRadar.getPlanes().get(j);
                if (arePlanesToClose(plane1.getCoordinates(), plane2.getCoordinates())) {
                    handleCollision(plane1, plane2);
                }
            }
        }
    }

    private void handleCollision(Plane plane1, Plane plane2) {
        String[] collidedIDs = {plane1.getFlightNumber(), plane2.getFlightNumber()};
        collisionRepository.registerCollisionToDB(collidedIDs);
        plane1.destroyPlane();
        plane2.destroyPlane();
        log.info("Collision detected between Plane [{}] and Plane [{}]", plane1.getFlightNumber(), plane2.getFlightNumber());
    }

    /*
     * Checks if two planes are too close to each other
     *
     * Because the animation uses scaled aircraft models, the system has been adjusted to their size
     * Introduced a 500-meter offset, ensuring collisions visually occur when the models actually touch
     *
     * In practice, if the horizontal distance <= 500 and the altitude difference <= 10,
     * it is considered a potential collision risk
     */
    private boolean arePlanesToClose(Coordinates loc1, Coordinates loc2) {
        double horizontalDistance = loc1.horizontalDistance(loc2);
        double altDiff = loc1.verticalDistance(loc2);
        return horizontalDistance <= HORIZONTAL_COLLISION_DISTANCE && altDiff <= ALTITUDE_COLLISION_DISTANCE;
    }

}

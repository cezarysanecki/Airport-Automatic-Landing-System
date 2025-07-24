package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Coordinates;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.repository.CollisionRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import static com.jakub.bone.config.Constant.ALTITUDE_COLLISION_DISTANCE;
import static com.jakub.bone.config.Constant.COLLISION_CHECK_DELAY;
import static com.jakub.bone.config.Constant.HORIZONTAL_COLLISION_DISTANCE;

@Log4j2
public class CollisionService extends Thread {

    private final ControlTowerService controlTowerService;
    private final CollisionRepository collisionRepository;

    public CollisionService(ControlTowerService controlTowerService, CollisionRepository collisionRepository) {
        this.controlTowerService = controlTowerService;
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
        for (int i = 0; i < controlTowerService.getPlanes().size(); i++) {
            Plane plane1 = controlTowerService.getPlanes().get(i);
            for (int j = i + 1; j < controlTowerService.getPlanes().size(); j++) {
                Plane plane2 = controlTowerService.getPlanes().get(j);
                if (arePlanesToClose(plane1.getNavigator().getCoordinates(), plane2.getNavigator().getCoordinates())) {
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
        double horizontalDistance = Math.sqrt(
                Math.pow(loc1.getX() - loc2.getX(), 2) +
                        Math.pow(loc1.getY() - loc2.getY(), 2)
        );
        double altDiff = Math.abs(loc1.getAltitude() - loc2.getAltitude());
        return horizontalDistance <= HORIZONTAL_COLLISION_DISTANCE && altDiff <= ALTITUDE_COLLISION_DISTANCE;
    }
}

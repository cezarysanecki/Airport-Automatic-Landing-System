package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.domain.plane.Plane;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.jooq.DSLContext;

import java.util.List;

import static com.jakub.bone.config.Constant.*;
import static jooq.Tables.COLLISIONS;

@Log4j2
public class CollisionService extends Thread {
    private ControlTowerService controlTowerService;
    private DSLContext context;

    public CollisionService(ControlTowerService controlTowerService){
        this.controlTowerService = controlTowerService;
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");
        while(true) {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    checkCollision();
                    Thread.sleep(COLLISION_CHECK_DELAY);
                }
            } catch (InterruptedException ex) {
                log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                break;
            }
        }
    }

    public void checkCollision() {
            for (int i = 0; i < controlTowerService.getPlanes().size(); i++) {
                Plane plane1 = controlTowerService.getPlanes().get(i);
                for (int j = i + 1; j < controlTowerService.getPlanes().size(); j++) {
                    Plane plane2 = controlTowerService.getPlanes().get(j);
                    if (arePlanesToClose(plane1.getNavigator().getLocation(), plane2.getNavigator().getLocation())) {
                        handleCollision(plane1, plane2);
                    }
                }
            }
    }

    private void handleCollision(Plane plane1, Plane plane2){
        String[] collidedIDs = {plane1.getFlightNumber(), plane2.getFlightNumber()};
        controlTowerService.getDatabase().getCOLLISION_REPOSITORY().registerCollisionToDB(collidedIDs);
        plane1.setDestroyed(true);
        plane2.setDestroyed(true);
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
    private boolean arePlanesToClose(Location loc1, Location loc2) {
        double horizontalDistance = Math.sqrt(
                Math.pow(loc1.getX() - loc2.getX(), 2) +
                        Math.pow(loc1.getY() - loc2.getY(), 2)
        );
        double altDiff = Math.abs(loc1.getAltitude() - loc2.getAltitude());
        return horizontalDistance <= HORIZONTAL_COLLISION_DISTANCE && altDiff <= ALTITUDE_COLLISION_DISTANCE;
    }

    // API function
    public List<String> getCollidedPlanes() {
        return context.select(COLLISIONS.INVOLVED_PLANES)
                .from(COLLISIONS)
                .where(COLLISIONS.TIME.isNotNull())
                .fetchInto(String.class);
    }
}

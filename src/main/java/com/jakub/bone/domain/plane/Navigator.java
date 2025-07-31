package com.jakub.bone.domain.plane;

import com.jakub.bone.shared.Coordinates;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jakub.bone.config.Constant.MAX_ALTITUDE;
import static com.jakub.bone.config.Constant.MIN_ALTITUDE;
import static com.jakub.bone.config.Constant.UPDATE_DELAY;

@Getter
@Setter
@Log4j2
public class Navigator {

    private List<Coordinates> waypoints;
    private Coordinates coordinates;
    private int currentIndex;

    private FuelManager fuelManager;
    private boolean isFirstMove;

    public Navigator(List<Coordinates> waypoints, FuelManager fuelManager) {
        this.waypoints = waypoints;
        this.fuelManager = fuelManager;
        this.isFirstMove = true;

        List<Coordinates> waypointsToSpawn = this.waypoints.stream()
                .filter(wp -> wp.getAltitude() >= MIN_ALTITUDE && wp.getAltitude() <= MAX_ALTITUDE)
                .toList();

        Random random = new Random();
        this.currentIndex = random.nextInt(waypointsToSpawn.size());
        this.coordinates = waypointsToSpawn.get(currentIndex);
    }

    public void move() {
        if (currentIndex < waypoints.size()) {
            updateLocation(waypoints.get(currentIndex));
            currentIndex++;
        }
        fuelManager.burnFuel();
    }

    public boolean isAtLastWaypoint() {
        return currentIndex == waypoints.size();
    }

    public List<Coordinates> getRiskZoneWaypoints() {
        List<Coordinates> nearWaypoints = new ArrayList<>();
        for (int offset = -3; offset <= 3; offset++) {
            int index = currentIndex + offset;
            if (index > 0 && index <= waypoints.size()) {
                nearWaypoints.add(waypoints.get(index));
            }
        }
        return nearWaypoints;
    }

    public void assignNewWaypoints(List<Coordinates> newWaypoints) {
        this.waypoints = newWaypoints;
        this.currentIndex = 0;
    }

    private void updateLocation(Coordinates coordinates) {
        if (!isFirstMove) {
            try {
                Thread.sleep(UPDATE_DELAY);
            } catch (InterruptedException ex) {
                log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }
        this.isFirstMove = false;
        this.coordinates = coordinates;
    }

}


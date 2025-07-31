package com.jakub.bone.domain.plane;

import com.jakub.bone.shared.Coordinates;

import java.util.List;
import java.util.Random;

import static com.jakub.bone.config.Constant.UPDATE_DELAY;

public class Waypoints {

    private static final Random RANDOM = new Random();

    private final List<Coordinates> coordinates;
    private int currentIndex;

    private Waypoints(List<Coordinates> coordinates, int currentIndex) {
        this.coordinates = coordinates;
        this.currentIndex = currentIndex;
    }

    public static Waypoints first(List<Coordinates> coordinates) {
        return new Waypoints(coordinates, 0);
    }

    public static Waypoints random(List<Coordinates> coordinates) {
        return new Waypoints(coordinates, RANDOM.nextInt(coordinates.size()));
    }

    public void resetToStart() {
        this.currentIndex = 0;
    }

    public Coordinates next() {
        try {
            Thread.sleep(UPDATE_DELAY);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        if (currentIndex < coordinates.size()) {
            currentIndex++;
            return coordinates.get(currentIndex - 1);
        }
        throw new IndexOutOfBoundsException("No more waypoints available");
    }

    public List<Coordinates> getNearestWaypointsTo(int range) {
        int start = Math.max(0, currentIndex - range);
        int end = Math.min(coordinates.size(), currentIndex + range);
        return coordinates.subList(start, end);
    }

    public boolean isLastWaypoint() {
        return currentIndex == coordinates.size();
    }

}

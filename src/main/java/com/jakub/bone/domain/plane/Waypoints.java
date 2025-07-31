package com.jakub.bone.domain.plane;

import com.jakub.bone.shared.Coordinates;

import java.util.List;
import java.util.Random;

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

    public Coordinates next() {
        if (currentIndex < coordinates.size()) {
            currentIndex++;
            return coordinates.get(currentIndex - 1);
        }
        throw new IndexOutOfBoundsException("No more waypoints available");
    }

}

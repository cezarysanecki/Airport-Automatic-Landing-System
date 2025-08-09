package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.domain.plane.PlaneNumber;
import com.jakub.bone.shared.CollisionAreaDetector;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static com.jakub.bone.config.Constant.MAX_CAPACITY;

@Log4j2
public class PlanesRadar {

    private final Lock planesLock = new ReentrantLock();
    private final Lock runwaysLock = new ReentrantLock();

    @Getter
    private final List<ServerPlane> planes = new CopyOnWriteArrayList<>();
    private final Map<Runway, Plane> runwayAssignment = new ConcurrentHashMap<>();

    public void registerPlane(Plane plane) {
        executeWithLock(() -> {
            ServerPlane serverPlane = new ServerPlane(plane);

            planes.add(serverPlane);

            log.info("Plane registered in memory: {}", serverPlane.getFlightNumber());
        });
    }

    public int countPlanes() {
        log.info("Current planes count: {}", planes.size());
        return planes.size();
    }

    public boolean isSpaceFull() {
        return executeWithLock(() -> planes.size() >= MAX_CAPACITY);
    }

    public boolean isAtCollisionRiskZone(ServerPlane plane) {
        return executeWithLock(() -> planes.stream()
                .anyMatch(otherPlane ->
                        CollisionAreaDetector.areClose(plane.getCoordinates(), otherPlane.getCoordinates())
                )
        );
    }

    public boolean isRunwayAvailable(Runway runway) {
        return executeWithLock(runway::isAvailable);
    }

    public void assignRunway(Runway runway) {
        executeWithLock(() -> runway.setAvailable(false));
    }

    public void releaseRunway(Runway runway) {
        executeWithLock(() -> runway.setAvailable(true));
    }

    public void removePlaneFromSpace(String flightNumber) {
        PlaneNumber planeNumber = new PlaneNumber(flightNumber);
        executeWithLock(() -> planes.removeIf(plane -> plane.getFlightNumber().equals(planeNumber)));
    }

    public boolean isPlanePresent(String flightNumber) {
        PlaneNumber planeNumber = new PlaneNumber(flightNumber);
        return planes.stream()
                .anyMatch(plane -> plane.getFlightNumber().equals(planeNumber));
    }

    public PlaneCoordinates getPlaneByFlightNumber(String flightNumber) {
        PlaneNumber planeNumber = new PlaneNumber(flightNumber);

        return executeWithLock(() -> planes.stream()
                .filter(plane -> plane.getFlightNumber().equals(planeNumber))
                .findFirst()
                .map(plane -> new PlaneCoordinates(
                        plane.getFlightNumber().value(),
                        plane.getCoordinates(),
                        plane.isLanding()
                ))
                .orElse(null));
    }

    public List<String> getAllFlightNumbers() {
        return executeWithLock(() -> {
            List<String> flightNumbers = new ArrayList<>();
            for (ServerPlane plane : planes) {
                flightNumbers.add(plane.getFlightNumber().value());
            }
            return flightNumbers;
        });
    }

    public List<PlaneCoordinates> getPlaneCoordinates() {
        return planes.stream()
                .map(plane -> new PlaneCoordinates(
                        plane.getFlightNumber().value(),
                        plane.getCoordinates(),
                        plane.isLanding()))
                .toList();
    }

    // Helper methods for locks management
    private <T> T executeWithLock(Supplier<T> action) {
        planesLock.lock();
        try {
            return action.get();
        } finally {
            planesLock.unlock();
        }
    }

    private void executeWithLock(Runnable action) {
        planesLock.lock();
        try {
            action.run();
        } finally {
            planesLock.unlock();
        }
    }
}
package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.Plane;
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
    private final List<Plane> planes = new CopyOnWriteArrayList<>();
    private final Map<Runway, Plane> runwayAssignment = new ConcurrentHashMap<>();

    public void registerPlane(Plane plane) {
        executeWithLock(() -> {
            planes.add(plane);
            log.info("Plane registered in memory: {}", plane.getFlightNumber());
        });
    }

    public int countPlanes() {
        int countFlyingPlanes = (int) planes.stream()
                .filter(plane -> !plane.isDestroyed() && !plane.isLanded())
                .count();
        log.info("Current planes count: {}", countFlyingPlanes);
        return countFlyingPlanes;
    }

    public boolean isSpaceFull() {
        return executeWithLock(() -> planes.size() >= MAX_CAPACITY);
    }

    public boolean isAtCollisionRiskZone(Plane plane) {
        return executeWithLock(() -> planes.stream()
                .anyMatch(otherPlane -> plane.getRiskZoneWaypoints().contains(otherPlane.getCoordinates())));
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
        executeWithLock(() -> planes.removeIf(p -> p.getFlightNumber().equals(flightNumber)));
    }

    public boolean isPlanePresent(String flightNumber) {
        return planes.stream()
                .filter(plane -> flightNumber.equals(plane.getFlightNumber()))
                .findFirst()
                .map(plane -> !plane.isDestroyed() && !plane.isLanded())
                .isPresent();
    }

    public PlaneCoordinates getPlaneByFlightNumber(String flightNumber) {
        return executeWithLock(() -> planes.stream()
                .filter(plane -> flightNumber.equals(plane.getFlightNumber()))
                .findFirst()
                .map(plane -> new PlaneCoordinates(
                        plane.getFlightNumber(),
                        plane.getCoordinates(),
                        plane.getPhase() == Plane.FlightPhase.LANDING
                ))
                .orElse(null));
    }

    public List<String> getAllFlightNumbers() {
        return executeWithLock(() -> {
            List<String> flightNumbers = new ArrayList<>();
            for (Plane plane : planes) {
                flightNumbers.add(plane.getFlightNumber());
            }
            return flightNumbers;
        });
    }

    public List<PlaneCoordinates> getPlaneCoordinates() {
        return planes.stream()
                .map(plane -> new PlaneCoordinates(
                        plane.getFlightNumber(),
                        plane.getCoordinates(),
                        plane.getPhase() == Plane.FlightPhase.LANDING))
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
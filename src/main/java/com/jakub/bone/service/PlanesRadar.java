package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.FlightPhase;
import com.jakub.bone.domain.plane.PlaneNumber;
import com.jakub.bone.plane.server.ServerPlane;
import com.jakub.bone.shared.CollisionAreaDetector;
import com.jakub.bone.shared.Coordinates;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.jakub.bone.config.Constant.MAX_CAPACITY;

@Log4j2
public class PlanesRadar {

    private final Lock planesLock = new ReentrantLock();

    private final ControlTower controlTower = new ControlTower();

    @Getter
    private final List<ServerPlane> planes = new CopyOnWriteArrayList<>();

    public void registerPlane(ServerPlane plane) {
        LockUtils.executeWithLock(planesLock, () -> {
            planes.add(plane);

            log.info("Plane [{}]: registered at {} ", plane.getFlightNumber(), plane.getCoordinates());
        });
    }

    public int countPlanes() {
        log.info("Current planes count: {}", planes.size());
        return planes.size();
    }

    public boolean isSpaceFull() {
        return LockUtils.executeWithLock(planesLock, () -> planes.size() >= MAX_CAPACITY);
    }

    public boolean isAtCollisionRiskZone(Coordinates coordinates) {
        return LockUtils.executeWithLock(planesLock, () -> planes.stream()
                .anyMatch(otherPlane ->
                        CollisionAreaDetector.areClose(coordinates, otherPlane.getCoordinates())
                )
        );
    }

    public boolean isRunwayAvailable(Runway runway) {
        return controlTower.isRunwayAvailable(runway);
    }

    public void assignRunway(Runway runway, PlaneNumber planeNumber) {
        controlTower.assignRunway(runway, planeNumber);
        log.info("Runway [{}] assigned to Plane [{}]", runway.getId(), planeNumber);
    }

    public void releaseRunway(PlaneNumber planeNumber) {
        boolean success = controlTower.releaseRunway(planeNumber);
        if (success) {
            log.info("Runway released by plane [{}]", planeNumber);
        }
    }

    public void removePlaneFromSpace(PlaneNumber planeNumber) {
        LockUtils.executeWithLock(planesLock, () -> planes.removeIf(plane -> plane.getFlightNumber().equals(planeNumber.value())));
    }

    public boolean isPlanePresent(PlaneNumber planeNumber) {
        return planes.stream()
                .anyMatch(plane -> plane.getFlightNumber().equals(planeNumber.value()));
    }

    public PlaneCoordinates getPlaneByFlightNumber(PlaneNumber planeNumber) {
        return LockUtils.executeWithLock(planesLock, () -> planes.stream()
                .filter(plane -> plane.getFlightNumber().equals(planeNumber.value()))
                .findFirst()
                .map(plane -> new PlaneCoordinates(
                        plane.getFlightNumber(),
                        plane.getCoordinates(),
                        plane.getPhase() == FlightPhase.LANDING
                ))
                .orElse(null));
    }

    public List<String> getAllFlightNumbers() {
        return LockUtils.executeWithLock(planesLock, () -> {
            List<String> flightNumbers = new ArrayList<>();
            for (ServerPlane plane : planes) {
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
                        plane.getPhase() == FlightPhase.LANDING))
                .toList();
    }

}
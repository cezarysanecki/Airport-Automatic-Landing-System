package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.database.AirportDatabase;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import com.jakub.bone.domain.plane.Plane;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static com.jakub.bone.config.Constant.*;

@Log4j2
@Getter
public class ControlTowerService {
    private List<Plane> planes;
    private Lock lock;
    private AirportDatabase database;

    public ControlTowerService(AirportDatabase database) throws SQLException {
        this.planes = new CopyOnWriteArrayList<>();
        this.lock = new ReentrantLock();
        this.database = database;
    }

    public void registerPlane(Plane plane) {
        executeWithLock(() -> {
            planes.add(plane);
            database.getPlaneRepository().registerPlaneInDB(plane);
        });
    }

    public boolean isSpaceFull() {
        return executeWithLock(() -> planes.size() >= MAX_CAPACITY);
    }


    public boolean isAtCollisionRiskZone(Plane plane) {
        return executeWithLock(() -> planes.stream()
                .anyMatch(otherPlane -> plane.getNavigator().getRiskZoneWaypoints()
                .contains(otherPlane.getNavigator().getCoordinates())));
    }

    public boolean isRunwayAvailable(Runway runway){
        return executeWithLock(runway::isAvailable);
    }

    public void assignRunway(Runway runway) {
        executeWithLock(() -> runway.setAvailable(false));
    }

    public void releaseRunway(Runway runway) {
        executeWithLock(() -> runway.setAvailable(true));
    }

    public void releaseRunwayIfPlaneAtFinalApproach(Plane plane, Runway runway){
        if(plane.getNavigator().getCoordinates().equals(runway.getCorridor().getFinalApproachPoint())){
            releaseRunway(runway);
        }
    }

    public void removePlaneFromSpace(Plane plane) {
        executeWithLock(() -> planes.remove(plane));
    }

    public boolean isPlaneApproachingHoldingAltitude(Plane plane) {
        return plane.getNavigator().getCoordinates().getAltitude() == HOLDING_ENTRY_ALTITUDE;
    }

    public boolean hasLandedOnRunway(Plane plane, Runway runway){
        boolean hasLanded = plane.getNavigator().getCoordinates().equals(runway.getLandingPoint());
        if (hasLanded) {
            database.getPlaneRepository().registerLandingInDB(plane);
        }
        return hasLanded;
    }

    public Plane getPlaneByFlightNumber(String flightNumber){
        return executeWithLock(() -> planes.stream()
                .filter(plane -> flightNumber.equals(plane.getFlightNumber()))
                .findFirst()
                .orElse(null));
    }

    public List<String> getAllFlightNumbers(){
        return executeWithLock(() -> {
            List<String> flightNumbers = new ArrayList<>();
            for (Plane plane : planes) {
                flightNumbers.add(plane.getFlightNumber());
            }
            return flightNumbers;
        });
    }

    // Helper methods for locks management
    private <T> T executeWithLock(Supplier<T> action){
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }

    private void executeWithLock(Runnable action){
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }
}
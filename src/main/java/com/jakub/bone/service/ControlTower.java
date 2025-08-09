package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.PlaneNumber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControlTower {

    private final Map<Runway, PlaneNumber> runwayAssignment = new ConcurrentHashMap<>();

    public boolean isRunwayAvailable(Runway runway) {
        return runwayAssignment.get(runway) == null;
    }

    public void assignRunway(Runway runway, PlaneNumber planeNumber) {
        runwayAssignment.put(runway, planeNumber);
    }

    public boolean releaseRunway(PlaneNumber planeNumber) {
        return runwayAssignment.entrySet().removeIf(entry -> entry.getValue().equals(planeNumber));
    }

}

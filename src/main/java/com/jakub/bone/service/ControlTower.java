package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.Plane;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControlTower {

    private final Map<Runway, Plane> runwayAssignment = new ConcurrentHashMap<>();

    public boolean isRunwayAvailable(Runway runway) {
        return runwayAssignment.get(runway) == null;
    }

    public void assignRunway(Runway runway, Plane plane) {
        runwayAssignment.put(runway, plane);
    }

    public void releaseRunway(Plane plane) {
        runwayAssignment.entrySet().removeIf(entry -> entry.getValue().equals(plane));
    }

}

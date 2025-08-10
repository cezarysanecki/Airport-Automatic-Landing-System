package com.jakub.bone.domain.plane;

import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.utils.WaypointGenerator;

import java.util.List;

public class PlaneFactory {

    public static Plane createPlane() {
        String flightNumber = PlaneNumberFactory.generateFlightNumber().value();
        List<Coordinates> descentWaypoints = WaypointGenerator.prepareDescendingWaypoints();
        Waypoints waypoints = Waypoints.random(descentWaypoints);
        return Plane.createPlane(flightNumber, FuelManager.initialFuelLevel(), waypoints);
    }

}

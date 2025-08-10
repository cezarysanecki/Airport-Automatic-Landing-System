package com.jakub.bone.domain.plane;

import com.jakub.bone.plane.model.ClientPlane;
import com.jakub.bone.domain.Coordinates;
import com.jakub.bone.domain.WaypointGenerator;

import java.util.List;

public class PlaneFactory {

    public static ClientPlane createPlane() {
        String flightNumber = PlaneNumberFactory.generateFlightNumber().value();
        List<Coordinates> descentWaypoints = WaypointGenerator.prepareDescendingWaypoints();
        Waypoints waypoints = Waypoints.random(descentWaypoints);
        return ClientPlane.createPlane(flightNumber, FuelManager.initialFuelLevel(), waypoints);
    }

}

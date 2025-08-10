package com.jakub.bone.domain.plane;

import com.jakub.bone.plane.client.ClientPlane;
import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.utils.WaypointGenerator;

import java.util.List;

public class PlaneFactory {

    public static ClientPlane createPlane() {
        String flightNumber = PlaneNumberFactory.generateFlightNumber().value();
        List<Coordinates> descentWaypoints = WaypointGenerator.prepareDescendingWaypoints();
        Waypoints waypoints = Waypoints.random(descentWaypoints);
        return ClientPlane.createPlane(flightNumber, FuelManager.initialFuelLevel(), waypoints);
    }

}

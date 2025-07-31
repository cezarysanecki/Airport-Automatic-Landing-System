package com.jakub.bone.domain.plane;

import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.utils.WaypointGenerator;

import java.util.List;

public class PlaneFactory {

    public static Plane createPlane() {
        String flightNumber = PlaneNumberFactory.generateFlightNumber().value();
        List<Coordinates> descentWaypoints = WaypointGenerator.getDescentWaypoints();
        Navigator navigator = new Navigator(descentWaypoints);
        return Plane.createPlane(flightNumber, navigator);
    }

}

package com.jakub.bone.api.monitoring;

import com.jakub.bone.domain.plane.Plane;

import java.util.LinkedHashMap;
import java.util.Map;

class PlanesMapper {
    static Map<String, Object> toMap(Plane plane) {
        Map<String, Object> planeMap = new LinkedHashMap<>();
        planeMap.put("flightNumber", plane.getFlightNumber());
        planeMap.put("phase", plane.getPhase());

        Map<String, Object> locationMap = new LinkedHashMap<>();
        locationMap.put("x", plane.getNavigator().getCoordinates().getX());
        locationMap.put("y", plane.getNavigator().getCoordinates().getY());
        locationMap.put("altitude", plane.getNavigator().getCoordinates().getAltitude());

        planeMap.put("location", locationMap);
        planeMap.put("fuel level", plane.getFuelManager().getFuelLevel());

        return planeMap;
    }
}

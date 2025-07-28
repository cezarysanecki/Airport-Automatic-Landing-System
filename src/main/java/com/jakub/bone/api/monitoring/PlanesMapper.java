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
        locationMap.put("x", plane.getCoordinates().getX());
        locationMap.put("y", plane.getCoordinates().getY());
        locationMap.put("altitude", plane.getCoordinates().getAltitude());

        planeMap.put("location", locationMap);
        planeMap.put("fuel level", plane.getFuelLevel());

        return planeMap;
    }
}

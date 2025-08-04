package com.jakub.bone.api.monitoring;

import com.jakub.bone.service.PlaneCoordinates;

import java.util.LinkedHashMap;
import java.util.Map;

class PlanesMapper {
    static Map<String, Object> toMap(PlaneCoordinates plane) {
        Map<String, Object> planeMap = new LinkedHashMap<>();
        planeMap.put("flightNumber", plane.flightNumber());

        Map<String, Object> locationMap = new LinkedHashMap<>();
        locationMap.put("x", plane.coordinates().getX());
        locationMap.put("y", plane.coordinates().getY());
        locationMap.put("altitude", plane.coordinates().getAltitude());

        planeMap.put("location", locationMap);
        planeMap.put("isLanding", plane.landing());

        return planeMap;
    }
}

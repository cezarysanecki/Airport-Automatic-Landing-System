package com.jakub.bone.airport.dto;

import com.jakub.bone.domain.Coordinates;

public record PlaneCoordinates(
        String flightNumber,
        Coordinates coordinates,
        boolean landing
) {
}

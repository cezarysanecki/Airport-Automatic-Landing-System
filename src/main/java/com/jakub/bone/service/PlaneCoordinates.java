package com.jakub.bone.service;

import com.jakub.bone.shared.Coordinates;

public record PlaneCoordinates(
        String flightNumber,
        Coordinates coordinates,
        boolean landing
) {
}

package com.jakub.bone.service;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.domain.plane.PlaneNumber;
import com.jakub.bone.shared.Coordinates;

public class ServerPlane {

    final Plane plane;

    public ServerPlane(Plane plane) {
        this.plane = plane;
    }

    public boolean isLanding() {
        return plane.getPhase() == Plane.FlightPhase.LANDING;
    }

    public Coordinates getCoordinates() {
        return plane.getCoordinates();
    }

    public PlaneNumber getFlightNumber() {
        return new PlaneNumber(plane.getFlightNumber());
    }

}

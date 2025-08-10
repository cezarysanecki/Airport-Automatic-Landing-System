package com.jakub.bone.airport.plane.model;

import com.jakub.bone.domain.plane.FlightPhase;
import com.jakub.bone.domain.plane.PlaneNumber;
import com.jakub.bone.domain.Coordinates;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

import static com.jakub.bone.config.Constant.HOLDING_ENTRY_ALTITUDE;

@Log4j2
@Getter
@Setter
public class ServerPlane implements Serializable {

    private String flightNumber;
    private boolean landed;
    private boolean isDestroyed;
    private FlightPhase phase;
    private Coordinates landingPoint;
    private Coordinates currentCoordinates;

    public ServerPlane(String flightNumber, boolean landed, boolean isDestroyed, FlightPhase phase, Coordinates landingPoint, Coordinates currentCoordinates) {
        this.flightNumber = flightNumber;
        this.landed = landed;
        this.isDestroyed = isDestroyed;
        this.phase = phase;
        this.landingPoint = landingPoint;
        this.currentCoordinates = currentCoordinates;
    }

    public boolean hasReached(Coordinates finalApproachPoint) {
        return currentCoordinates.equals(finalApproachPoint);
    }

    public void changePhase(FlightPhase newPhase) {
        if (this.phase != newPhase) {
            log.info("Plane [{}]: transitioned to phase: {}", flightNumber, newPhase);
            this.phase = newPhase;
        }
    }

    public void destroyPlane() {
        this.isDestroyed = true;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.currentCoordinates = coordinates;
    }

    public boolean isPlaneApproachingHoldingAltitude() {
        return currentCoordinates.getAltitude() == HOLDING_ENTRY_ALTITUDE;
    }

    public Coordinates getCoordinates() {
        return currentCoordinates;
    }

    public PlaneNumber getPlaneNumber() {
        return new PlaneNumber(flightNumber);
    }

}

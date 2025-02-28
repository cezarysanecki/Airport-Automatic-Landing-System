package com.jakub.bone.client;

import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utils.Messenger;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectOutputStream;

@Log4j2
public class PlaneCommunicationService {
    private Plane plane;
    private Messenger messenger;
    private ObjectOutputStream out;

    public PlaneCommunicationService(Plane plane, Messenger messenger, ObjectOutputStream out) {
        this.plane = plane;
        this.messenger = messenger;
        this.out = out;
    }

    public void sendInitialData() throws IOException {
        sendData(plane);
    }

    public boolean sendFuelLevel() throws IOException {
        double fuelLevel = plane.getFuelManager().getFuelLevel();
        sendData(fuelLevel);

        if (plane.getFuelManager().isOutOfFuel()) {
            log.info("Plane [{}]: out of fuel. Collision detected", plane.getFlightNumber());
            return false;
        }
        return true;
    }

    public boolean sendLocation() throws IOException {
        Location location = plane.getNavigator().getLocation();

        if(location == null) {
            log.info("Plane [{}]: disappeared from the radar", plane.getFlightNumber());
            return false;
        }

        sendData(location);
        return true;
    }

    private void sendData(Object data) throws IOException {
        messenger.send(data, out);
        out.flush();
    }
}

package com.jakub.bone.client;

import com.jakub.bone.domain.airport.Coordinates;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utils.Messenger;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectOutputStream;

@Log4j2
public class PlaneCommunicationService {

    private final ObjectOutputStream out;

    public PlaneCommunicationService(ObjectOutputStream out) {
        this.out = out;
    }

    public void sendInitialData(Plane plane) throws IOException {
        Messenger.send(out, plane);
        out.flush();
    }

    public void sendFuelLevel(double fuelLevel) throws IOException {
        Messenger.send(out, fuelLevel);
        out.flush();
    }

    public void sendLocation(Coordinates coordinates) throws IOException {
        Messenger.send(out, coordinates);
        out.flush();
    }

}

package com.jakub.bone.plane.infrastructure;

import com.jakub.bone.airport.plane.infrastructure.AssignRunwayMessage;
import com.jakub.bone.infrastructure.Messenger;
import com.jakub.bone.airport.plane.model.AirportInstruction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlaneClientMessanger {

    public static void send(ObjectOutputStream out, RegisterPlaneMessage message) throws IOException {
        Messenger.sendGeneric(out, message);
    }

    public static void send(ObjectOutputStream out, UpdatePlaneStateMessage message) throws IOException {
        Messenger.sendGeneric(out, message);
    }

    public static AssignRunwayMessage handleAssignRunwayMessage(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, AssignRunwayMessage.class);
    }

    public static AirportInstruction handleResponseAirportInstruction(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, AirportInstruction.class);
    }
}

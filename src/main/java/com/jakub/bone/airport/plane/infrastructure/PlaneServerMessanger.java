package com.jakub.bone.airport.plane.infrastructure;

import com.jakub.bone.infrastructure.Messenger;
import com.jakub.bone.plane.infrastructure.RegisterPlaneMessage;
import com.jakub.bone.plane.infrastructure.UpdatePlaneStateMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.jakub.bone.airport.plane.model.AirportInstruction.COLLISION;
import static com.jakub.bone.airport.plane.model.AirportInstruction.DESCENT;
import static com.jakub.bone.airport.plane.model.AirportInstruction.FULL;
import static com.jakub.bone.airport.plane.model.AirportInstruction.HOLD_PATTERN;
import static com.jakub.bone.airport.plane.model.AirportInstruction.LAND;
import static com.jakub.bone.airport.plane.model.AirportInstruction.RISK_ZONE;

public class PlaneServerMessanger {

    public static void sendAssignRunwayMessage(ObjectOutputStream out, AssignRunwayMessage message) throws IOException {
        Messenger.sendGeneric(out, message);
    }

    public static void sendDescentCommand(ObjectOutputStream out) throws IOException {
        Messenger.sendGeneric(out, DESCENT);
    }

    public static void sendHoldPatternCommand(ObjectOutputStream out) throws IOException {
        Messenger.sendGeneric(out, HOLD_PATTERN);
    }

    public static void sendLandCommand(ObjectOutputStream out) throws IOException {
        Messenger.sendGeneric(out, LAND);
    }

    public static void sendFullCommand(ObjectOutputStream out) throws IOException {
        Messenger.sendGeneric(out, FULL);
    }

    public static void sendCollisionCommand(ObjectOutputStream out) throws IOException {
        Messenger.sendGeneric(out, COLLISION);
    }

    public static void sendRiskZoneCommand(ObjectOutputStream out) throws IOException {
        Messenger.sendGeneric(out, RISK_ZONE);
    }

    public static UpdatePlaneStateMessage handleUpdatePlaneStateMessage(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, UpdatePlaneStateMessage.class);
    }

    public static RegisterPlaneMessage handleRegisterPlaneMessage(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, RegisterPlaneMessage.class);
    }
}

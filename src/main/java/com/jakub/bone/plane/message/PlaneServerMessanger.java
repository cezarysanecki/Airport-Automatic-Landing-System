package com.jakub.bone.plane.message;

import com.jakub.bone.plane.message.structures.AssignRunwayMessage;
import com.jakub.bone.plane.message.structures.RegisterPlaneMessage;
import com.jakub.bone.plane.message.structures.UpdatePlaneStateMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlaneServerMessanger {

    public static RegisterPlaneMessage handleRegisterPlaneMessage(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, RegisterPlaneMessage.class);
    }

    public static void send(ObjectOutputStream out, AssignRunwayMessage message) throws IOException {
        Messenger.sendGeneric(out, message);
    }

    public static UpdatePlaneStateMessage handleUpdatePlaneStateMessage(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, UpdatePlaneStateMessage.class);
    }
}

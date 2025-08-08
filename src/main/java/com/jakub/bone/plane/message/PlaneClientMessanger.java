package com.jakub.bone.plane.message;

import com.jakub.bone.plane.message.structures.AssignRunwayMessage;
import com.jakub.bone.plane.message.structures.RegisterPlaneMessage;
import com.jakub.bone.plane.message.structures.UpdatePlaneStateMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlaneClientMessanger {

    public static void send(ObjectOutputStream out, RegisterPlaneMessage message) throws IOException {
        Messenger.sendGeneric(out, message);
    }

    public static AssignRunwayMessage handleAssignRunwayMessage(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, AssignRunwayMessage.class);
    }

    public static void send(ObjectOutputStream out, UpdatePlaneStateMessage message) throws IOException {
        Messenger.sendGeneric(out, message);
    }
}

package com.jakub.bone.plane.message;

import com.jakub.bone.plane.message.structures.AssignRunwayMessage;
import com.jakub.bone.plane.message.structures.RegisterPlaneMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlaneClientMessanger {

    public static void send(ObjectOutputStream out, RegisterPlaneMessage message) throws IOException {
        Messenger.sendGeneric(out, message);
    }

    public static AssignRunwayMessage handleResponseRunway(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, AssignRunwayMessage.class);
    }
}

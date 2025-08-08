package com.jakub.bone.plane.message;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class PlaneClientMessanger {

    public static void send(ObjectOutputStream out, RegisterPlaneMessage message) throws IOException {
        Messenger.sendGeneric(out, message);
    }
}

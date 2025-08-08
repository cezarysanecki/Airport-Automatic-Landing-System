package com.jakub.bone.plane.message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class PlaneHandlerMessanger {

    public static RegisterPlaneMessage handleResponsePlane(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return Messenger.handleResponse(in, RegisterPlaneMessage.class);
    }
}

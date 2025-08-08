package com.jakub.bone.plane.message.structures;

import com.jakub.bone.domain.plane.Plane;

public class RegisterPlaneMessage {

    public Plane plane;

    public RegisterPlaneMessage() {
    }

    public RegisterPlaneMessage(Plane plane) {
        this.plane = plane;
    }

}

package com.jakub.bone.plane.client;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utils.Messenger;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectOutputStream;

@Log4j2
public class PlaneStateSender {

    private final ObjectOutputStream out;

    public PlaneStateSender(ObjectOutputStream out) {
        this.out = out;
    }

    public void update(Plane plane) throws IOException {
        Messenger.send(out, plane.getFuelLevel());
        out.flush();

        if (plane.getCoordinates() != null) {
            Messenger.send(out, plane.getCoordinates());
            out.flush();
        }
    }

}

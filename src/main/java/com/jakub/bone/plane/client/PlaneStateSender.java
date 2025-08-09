package com.jakub.bone.plane.client;

import com.jakub.bone.plane.message.PlaneClientMessanger;
import com.jakub.bone.plane.message.structures.UpdatePlaneStateMessage;
import com.jakub.bone.shared.Coordinates;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectOutputStream;

@Log4j2
public class PlaneStateSender {

    private final ObjectOutputStream out;

    public PlaneStateSender(ObjectOutputStream out) {
        this.out = out;
    }

    public void update(Coordinates coordinates, double fuelLevel) throws IOException {
        PlaneClientMessanger.send(out, new UpdatePlaneStateMessage(coordinates, fuelLevel));
        out.flush();
    }

}

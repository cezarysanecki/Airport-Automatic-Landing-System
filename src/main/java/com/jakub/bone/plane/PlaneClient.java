package com.jakub.bone.plane;

import com.jakub.bone.domain.plane.PlaneFactory;
import com.jakub.bone.infrastructure.SocketClient;
import com.jakub.bone.plane.infrastructure.PlaneClientMessanger;
import com.jakub.bone.plane.infrastructure.RegisterPlaneMessage;
import com.jakub.bone.plane.infrastructure.UpdatePlaneStateMessage;
import com.jakub.bone.plane.model.ClientPlane;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Log4j2
@Getter
public class PlaneClient implements Runnable {

    private final SocketClient socketClient;
    private final ClientPlane plane;

    public PlaneClient(String ip, int port) {
        this.socketClient = new SocketClient(ip, port);
        this.plane = PlaneFactory.createPlane();

        log.debug("PlaneClient created for Plane [{}] at IP: {}, Port: {}", this.plane.getFlightNumber(), ip, port);
    }

    @Override
    public void run() {
        connectAndHandle();
    }

    private void connectAndHandle() {
        ThreadContext.put("type", "Client");
        try {
            socketClient.startConnection();

            ObjectInputStream in = socketClient.getIn();
            ObjectOutputStream out = socketClient.getOut();

            PlaneInstructionProcessorClient planeInstructionProcessorClient = new PlaneInstructionProcessorClient(plane, in, out);

            PlaneClientMessanger.send(out, new RegisterPlaneMessage(plane.getFlightNumber(), plane.isLanded(), plane.isDestroyed(), plane.getPhase(), plane.getLandingPoint(), plane.getCurrentCoordinates()));
            out.flush();

            while (!planeInstructionProcessorClient.isProcessCompleted()) {
                PlaneClientMessanger.send(out, new UpdatePlaneStateMessage(plane.getCoordinates(), plane.getFuelLevel()));

                if (plane.isOutOfFuel() || plane.getCoordinates() == null) {
                    log.error("Plane [{}]: lost communication due to fuel or location issues", plane.getFlightNumber());
                    break;
                }

                planeInstructionProcessorClient.processInstruction();

                if (plane.isDestroyed()) {
                    log.info("Plane [{}]: has destroyed", plane.getFlightNumber());
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            log.error("PlaneClient [{}]: encountered an error: {}", plane.getFlightNumber(), ex.getMessage(), ex);
        } finally {
            socketClient.stopConnection();
        }
    }

}

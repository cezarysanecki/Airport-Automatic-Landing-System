package com.jakub.bone.plane.client;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.infrastructure.SocketClient;
import com.jakub.bone.plane.message.PlaneClientMessanger;
import com.jakub.bone.plane.message.structures.RegisterPlaneMessage;
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
    private final Plane plane;

    public PlaneClient(String ip, int port, Plane plane) {
        this.socketClient = new SocketClient(ip, port);
        this.plane = plane;

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

            PlaneStateSender planeStateSender = new PlaneStateSender(out);
            PlaneInstructionProcessorClient planeInstructionProcessorClient = new PlaneInstructionProcessorClient(plane, in, out);

            PlaneClientMessanger.send(out, new RegisterPlaneMessage(plane));
            out.flush();

            while (!planeInstructionProcessorClient.isProcessCompleted()) {
                planeStateSender.update(plane.getCoordinates(), plane.getFuelLevel());
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
            closeConnection();
        }
    }

    private void closeConnection() {
        socketClient.stopConnection();
        log.debug("Plane [{}]: connection stopped", plane.getFlightNumber());
    }

}

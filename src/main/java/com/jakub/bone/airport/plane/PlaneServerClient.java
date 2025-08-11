package com.jakub.bone.airport.plane;

import com.jakub.bone.airport.PlanesRadar;
import com.jakub.bone.airport.plane.infrastructure.PlaneServerMessanger;
import com.jakub.bone.airport.plane.model.ServerPlane;
import com.jakub.bone.infrastructure.SocketClient;
import com.jakub.bone.plane.infrastructure.RegisterPlaneMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Log4j2
public class PlaneServerClient extends Thread {

    private final SocketClient socket;
    private final PlanesRadar planesRadar;
    private final PlanePhaseProcessorServer planePhaseProcessorServer;

    public PlaneServerClient(
            Socket socket,
            PlanesRadar planesRadar
    ) {
        this.socket = SocketClient.create(socket);
        this.planesRadar = planesRadar;
        this.planePhaseProcessorServer = new PlanePhaseProcessorServer(planesRadar);
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");

        try {
            socket.startConnection();

            ObjectInputStream in = socket.getIn();
            ObjectOutputStream out = socket.getOut();

            handleClient(in, out);
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage(), ex);
        } finally {
            socket.stopConnection();
        }
    }

    private void handleClient(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        RegisterPlaneMessage message = PlaneServerMessanger.handleRegisterPlaneMessage(in);
        ServerPlane plane = new ServerPlane(message.flightNumber, message.landed, message.isDestroyed, message.phase, message.landingPoint, message.currentCoordinates);

        if (planesRadar.isSpaceFull()) {
            PlaneServerMessanger.sendFullCommand(out);
            log.info("Plane [{}]: no capacity in airspace", plane.getFlightNumber());
            return;
        }

        if (planesRadar.isAtCollisionRiskZone(plane.getCoordinates())) {
            PlaneServerMessanger.sendRiskZoneCommand(out);
            log.info("Plane [{}]: collision risk zone - redirect to other airport", plane.getFlightNumber());
            return;
        }

        planesRadar.registerPlane(plane);

        PlaneHandlerLoop loop = new PlaneHandlerLoop(plane, in, out, planePhaseProcessorServer, planesRadar);

        loop.run();
    }

}



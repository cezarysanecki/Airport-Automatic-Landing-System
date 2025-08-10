package com.jakub.bone.plane.server;

import com.jakub.bone.plane.message.PlaneServerMessanger;
import com.jakub.bone.plane.message.structures.RegisterPlaneMessage;
import com.jakub.bone.service.PlanesRadar;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

@Log4j2
public class PlaneHandlerServer extends Thread {

    private final Socket clientSocket;
    private final PlanesRadar planesRadar;
    private final PlanePhaseProcessorServer planePhaseProcessorServer;

    public PlaneHandlerServer(
            Socket clientSocket,
            PlanesRadar planesRadar,
            PlanePhaseProcessorServer planePhaseProcessorServer
    ) {
        this.clientSocket = clientSocket;
        this.planesRadar = planesRadar;
        this.planePhaseProcessorServer = planePhaseProcessorServer;
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");
        log.info("Server started: {}", clientSocket.toString());

        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            handleClient(in, out);
        } catch (EOFException | SocketException ex) {
            log.warn("Connection to client lost. Client disconnected: {}", ex.getMessage(), ex);
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage(), ex);
        } finally {
            closeResources(in, out);
            try {
                clientSocket.close();
            } catch (IOException ex) {
                log.error("Failed to close client socket: {}", ex.getMessage(), ex);
            }
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

    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ex) {
                    log.error("Failed to close resource: {}", ex.getMessage(), ex);
                }
            }
        }
    }
}



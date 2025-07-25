package com.jakub.bone.infrastructure;

import com.jakub.bone.application.PlaneHandler;
import com.jakub.bone.client.Client;
import com.jakub.bone.client.PlaneCommunicationService;
import com.jakub.bone.client.PlaneInstructionHandler;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utils.Messenger;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;

@Log4j2
@Getter
public class PlaneClient implements Runnable {

    private final Client client;
    private final Plane plane;
    private final Messenger messenger;
    private PlaneInstructionHandler instructionHandler;
    private PlaneCommunicationService communicationService;

    public PlaneClient(String ip, int port, Messenger messenger, Plane plane) {
        this.client = new Client(ip, port);
        this.plane = plane;
        this.messenger = messenger;

        log.debug("PlaneClient created for Plane [{}] at IP: {}, Port: {}", this.plane.getFlightNumber(), ip, port);
    }

    @Override
    public void run() {
        connectAndHandle();
    }

    private void connectAndHandle() {
        ThreadContext.put("type", "Client");
        try {
            establishConnection();
            initializeServices();
            communicationService.sendInitialData();
            processInstructions();
        } catch (IOException | ClassNotFoundException ex) {
            log.error("PlaneClient [{}]: encountered an error: {}", plane.getFlightNumber(), ex.getMessage(), ex);
        } finally {
            closeConnection();
        }
    }

    private void establishConnection() throws IOException {
        client.startConnection();
        if (!client.isConnected()) {
            throw new IOException("Unable to establish connection to the server");
        }
        log.info("PlaneClient [{}]: connected to server", plane.getFlightNumber());
    }

    private void initializeServices() {
        this.communicationService = new PlaneCommunicationService(plane, messenger, client.getOut());
        this.instructionHandler = new PlaneInstructionHandler(plane, messenger, client.getIn(), client.getOut());
    }

    private void processInstructions() throws IOException, ClassNotFoundException {
        while (!instructionHandler.isProcessCompleted()) {
            if (!communicationService.sendFuelLevel() || !communicationService.sendLocation()) {
                log.error("Plane [{}]: lost communication due to fuel or location issues", plane.getFlightNumber());
                return;
            }

            PlaneHandler.AirportInstruction instruction = messenger.receiveAndParse(client.getIn(), PlaneHandler.AirportInstruction.class);
            instructionHandler.processInstruction(instruction);

            if (plane.isDestroyed()) {
                log.info("Plane [{}]: has destroyed", plane.getFlightNumber());
                return;
            }
        }
    }

    private void closeConnection() {
        client.stopConnection();
        log.debug("Plane [{}]: connection stopped", plane.getFlightNumber());
    }

}

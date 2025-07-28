package com.jakub.bone.infrastructure;

import com.jakub.bone.application.PlaneHandler;
import com.jakub.bone.client.PlaneCommunicationService;
import com.jakub.bone.client.PlaneInstructionHandler;
import com.jakub.bone.client.SocketClient;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utils.Messenger;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;

@Log4j2
@Getter
public class PlaneClient implements Runnable {

    private final SocketClient socketClient;
    private final Plane plane;
    private PlaneInstructionHandler instructionHandler;
    private PlaneCommunicationService communicationService;

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
            establishConnection();
            initializeServices();
            communicationService.sendInitialData(plane);
            processInstructions();
        } catch (IOException | ClassNotFoundException ex) {
            log.error("PlaneClient [{}]: encountered an error: {}", plane.getFlightNumber(), ex.getMessage(), ex);
        } finally {
            closeConnection();
        }
    }

    private void establishConnection() throws IOException {
        socketClient.startConnection();
        log.info("PlaneClient [{}]: connected to server", plane.getFlightNumber());
    }

    private void initializeServices() {
        this.communicationService = new PlaneCommunicationService(socketClient.getOut());
        this.instructionHandler = new PlaneInstructionHandler(plane, socketClient.getIn(), socketClient.getOut());
    }

    private void processInstructions() throws IOException, ClassNotFoundException {
        while (!instructionHandler.isProcessCompleted()) {
            communicationService.sendFuelLevel(plane.getFuelLevel());

            if (plane.isOutOfFuel() || plane.getCoordinates() == null) {
                log.error("Plane [{}]: lost communication due to fuel or location issues", plane.getFlightNumber());
                return;
            }
            communicationService.sendLocation(plane.getCoordinates());

            PlaneHandler.AirportInstruction instruction = Messenger.handleResponseAirportInstruction(socketClient.getIn());
            instructionHandler.processInstruction(instruction);

            if (plane.isDestroyed()) {
                log.info("Plane [{}]: has destroyed", plane.getFlightNumber());
                return;
            }
        }
    }

    private void closeConnection() {
        socketClient.stopConnection();
        log.debug("Plane [{}]: connection stopped", plane.getFlightNumber());
    }

}

package com.jakub.bone.runners;

import com.jakub.bone.application.PlaneHandler;
import com.jakub.bone.client.Client;
import com.jakub.bone.client.PlaneCommunicationService;
import com.jakub.bone.client.PlaneInstructionHandler;
import com.jakub.bone.config.Constant;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utils.Messenger;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
@Getter
public class PlaneClient extends Client implements Runnable {
    private final Plane plane;
    private final Messenger messenger;
    private PlaneInstructionHandler instructionHandler;
    private PlaneCommunicationService communicationService;

    public PlaneClient(String ip, int port) {
        super(ip, port);
        this.plane = new Plane(generateFlightNumber());
        this.messenger = new Messenger();
        log.debug("PlaneClient created for Plane [{}] at IP: {}, Port: {}", plane.getFlightNumber(), ip, port);
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
        startConnection();
        if (!isConnected) {
            throw new IOException("Unable to establish connection to the server");
        }
        log.info("PlaneClient [{}]: connected to server", plane.getFlightNumber());
    }

    private void initializeServices() {
        this.communicationService = new PlaneCommunicationService(plane, messenger, out);
        this.instructionHandler = new PlaneInstructionHandler(plane, messenger, in, out);
    }

    private void processInstructions() throws IOException, ClassNotFoundException {
        while (!instructionHandler.isProcessCompleted()) {
            if (!communicationService.sendFuelLevel() || !communicationService.sendLocation()) {
                log.error("Plane [{}]: lost communication due to fuel or location issues", plane.getFlightNumber());
                return;
            }

            PlaneHandler.AirportInstruction instruction = messenger.receiveAndParse(in, PlaneHandler.AirportInstruction.class);
            instructionHandler.processInstruction(instruction);

            if (plane.isDestroyed()) {
                log.info("Plane [{}]: has destroyed", plane.getFlightNumber());
                return;
            }
        }
    }

    public String generateFlightNumber() {
        String[] airlineCodes = {"MH", "AA", "BA", "LH", "AF", "EK", "QR", "KL", "UA", "DL"};
        String code = airlineCodes[ThreadLocalRandom.current().nextInt(airlineCodes.length)];
        int number = ThreadLocalRandom.current().nextInt(100, 999);
        return code + number;
    }

    private void closeConnection() {
        stopConnection();
        log.debug("Plane [{}]: connection stopped", plane.getFlightNumber());
    }

    public static void main(String[] args) throws IOException {
        int numberOfClients = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            PlaneClient client = new PlaneClient("localhost", 5000);
            try {
                Thread.sleep(Constant.CLIENT_SPAWN_DELAY);
            } catch (InterruptedException ex) {
                log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
            executorService.execute(client);
        }
        executorService.shutdown();
    }
}

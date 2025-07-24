package com.jakub.bone.service;

import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.runners.PlaneClient;
import lombok.Getter;

import java.io.IOException;

import static com.jakub.bone.config.Constant.CLIENT_SPAWN_DELAY;
import static com.jakub.bone.config.Constant.SERVER_INIT_DELAY;

/*
 * The class manages the startup of the AirportServer
 * Initializes of PlaneClient instances to simulate air traffic
 * Spawns multiple client instances at defined intervals
 */
@Getter
public class AirportStateService {

    private final AirportServer airportServer;

    public AirportStateService(AirportServer airportServer) {
        this.airportServer = airportServer;
    }

    public void startAirport() {
        if (airportServer.isRunning()) {
            return;
        }

        Thread serverThread = new Thread(() -> {
            try {
                this.airportServer.startServer(5000);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to initialize AirportServer due to I/O issues", ex);
            }
        });
        serverThread.start();

        // Wait for the server to initialize before proceeding
        while (airportServer.getControlTowerService() == null) {
            try {
                Thread.sleep(SERVER_INIT_DELAY);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                PlaneClient client = new PlaneClient("localhost", 5000);
                new Thread(client).start();

                try {
                    Thread.sleep(CLIENT_SPAWN_DELAY);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}

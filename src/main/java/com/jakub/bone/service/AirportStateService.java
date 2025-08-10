package com.jakub.bone.service;

import com.jakub.bone.config.ServerConstants;
import com.jakub.bone.plane.client.PlaneClient;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.runners.AirportServer;

import java.io.IOException;
import java.net.ServerSocket;

import static com.jakub.bone.config.Constant.CLIENT_SPAWN_DELAY;

/*
 * The class manages the startup of the AirportServer
 * Initializes of PlaneClient instances to simulate air traffic
 * Spawns multiple client instances at defined intervals
 */
public class AirportStateService {

    private final AirportServer airportServer;
    private final PlanesRadar planesRadar;
    private final CollisionRepository collisionRepository;

    public AirportStateService(AirportServer airportServer, PlanesRadar planesRadar, CollisionRepository collisionRepository) {
        this.airportServer = airportServer;
        this.planesRadar = planesRadar;
        this.collisionRepository = collisionRepository;
    }

    public void startAirport(ServerSocket serverSocket) {
        if (airportServer.isRunning()) {
            return;
        }

        Thread serverThread = new Thread(() -> {
            CollisionService collisionService = new CollisionService(planesRadar, collisionRepository);
            collisionService.start();

            try {
                this.airportServer.startServer(serverSocket);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to initialize AirportServer due to I/O issues", ex);
            }
        });
        serverThread.start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                PlaneClient client = new PlaneClient(ServerConstants.IP, ServerConstants.PORT);

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

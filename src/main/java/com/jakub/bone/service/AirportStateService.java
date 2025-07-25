package com.jakub.bone.service;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.domain.plane.PlaneNumberFactory;
import com.jakub.bone.infrastructure.PlaneClient;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.utils.Messenger;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;

import static com.jakub.bone.config.Constant.CLIENT_SPAWN_DELAY;

/*
 * The class manages the startup of the AirportServer
 * Initializes of PlaneClient instances to simulate air traffic
 * Spawns multiple client instances at defined intervals
 */
@Getter
public class AirportStateService {

    private final AirportServer airportServer;
    private final ControlTowerService controlTowerService;
    private final CollisionRepository collisionRepository;

    public AirportStateService(AirportServer airportServer, ControlTowerService controlTowerService, CollisionRepository collisionRepository) {
        this.airportServer = airportServer;
        this.controlTowerService = controlTowerService;
        this.collisionRepository = collisionRepository;
    }

    public void startAirport(ServerSocket serverSocket) {
        if (airportServer.isRunning()) {
            return;
        }

        Messenger messenger = new Messenger();

        Thread serverThread = new Thread(() -> {
            CollisionService collisionService = new CollisionService(controlTowerService, collisionRepository);
            collisionService.start();

            try {
                this.airportServer.startServer(serverSocket, messenger);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to initialize AirportServer due to I/O issues", ex);
            }
        });
        serverThread.start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                Plane plane = new Plane(PlaneNumberFactory.generateFlightNumber().value());
                PlaneClient client = new PlaneClient("localhost", 5000, messenger, plane);

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

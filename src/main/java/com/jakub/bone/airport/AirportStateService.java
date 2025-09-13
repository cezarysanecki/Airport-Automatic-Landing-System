package com.jakub.bone.airport;

import com.jakub.bone.config.ServerConstants;
import com.jakub.bone.plane.PlaneClient;
import com.jakub.bone.repository.CollisionRepository;

import java.io.IOException;
import java.net.ServerSocket;

import static com.jakub.bone.config.Constant.CLIENT_SPAWN_DELAY;

/*
 * The class manages the startup of the AirportServer
 * Initializes of PlaneClient instances to simulate air traffic
 * Spawns multiple client instances at defined intervals
 */
public class AirportStateService {

    private final AirportMainServer airportMainServer;
    private final PlanesRadar planesRadar;
    private final CollisionRepository collisionRepository;

    public AirportStateService(AirportMainServer airportMainServer, PlanesRadar planesRadar, CollisionRepository collisionRepository) {
        this.airportMainServer = airportMainServer;
        this.planesRadar = planesRadar;
        this.collisionRepository = collisionRepository;
    }

    public void startAirport(ServerSocket serverSocket) {
        if (airportMainServer.isRunning()) {
            return;
        }

        Thread serverThread = new Thread(() -> {
            CollisionService collisionService = new CollisionService(planesRadar, collisionRepository);
            collisionService.start();

            try {
                this.airportMainServer.startServer(serverSocket);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to initialize AirportServer due to I/O issues", ex);
            }
        });
        serverThread.start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    PlaneClient client = new PlaneClient(ServerConstants.IP, ServerConstants.PORT);

                    new Thread(client).start();
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to create PlaneClient due to I/O issues", ex);
                }


                try {
                    Thread.sleep(CLIENT_SPAWN_DELAY);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}

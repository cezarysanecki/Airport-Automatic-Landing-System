package com.jakub.bone.runners;

import com.jakub.bone.application.PlaneHandler;
import com.jakub.bone.config.DbConstants;
import com.jakub.bone.config.ServerConstants;
import com.jakub.bone.service.CollisionService;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.service.FlightPhaseService;
import com.jakub.bone.utils.Messenger;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

@Log4j2
public class AirportServer {

    private final ControlTowerService controlTowerService;

    @Getter
    private boolean running;
    @Getter
    private boolean paused;
    @Getter
    private Instant startTime;

    public AirportServer(
            ControlTowerService controlTowerService
    ) throws SQLException {
        this.controlTowerService = controlTowerService;

        this.running = false;
        this.paused = false;
    }

    public void startServer(ServerSocket serverSocket, Messenger messenger) throws IOException {
        ThreadContext.put("type", "Server");
        running = true;

        try {
            this.startTime = Instant.now();
            log.info("Server started");


            log.info("Collision detector started");

            while (true) {
                if (paused) {
                    Thread.sleep(2000);
                    log.info("Airport paused. Waiting...");
                    continue;
                }

                log.debug("Server connected with client at port: {}", serverSocket.getLocalPort());
                running = true;

                FlightPhaseService phaseCoordinator = new FlightPhaseService(controlTowerService, messenger);
                PlaneHandler planeHandler = new PlaneHandler(
                        serverSocket,
                        controlTowerService,
                        messenger,
                        phaseCoordinator
                );

                planeHandler.start();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        running = false;
    }

    public void pauseServer() {
        this.paused = true;
    }

    public void resumeServer() {
        this.paused = false;
    }

    public Duration getUptime() {
        return Duration.between(startTime, Instant.now());
    }

    public static void main(String[] args) throws IOException, SQLException {
        try (Connection connection = DriverManager.getConnection(DbConstants.URL, DbConstants.USER, DbConstants.PASSWORD)) {
            AirportServerFactory airportServerFactory = new AirportServerFactory(connection);

            try (ServerSocket serverSocket = new ServerSocket(ServerConstants.PORT)) {
                CollisionService collisionService = new CollisionService(
                        airportServerFactory.controlTowerService,
                        airportServerFactory.collisionRepository
                );
                collisionService.start();

                Messenger messenger = new Messenger();
                airportServerFactory.airportServer.startServer(serverSocket, messenger);
            }

        }
    }
}

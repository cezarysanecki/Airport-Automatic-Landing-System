package com.jakub.bone.runners;

import com.jakub.bone.application.PlaneHandler;
import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.service.CollisionService;
import com.jakub.bone.service.ControlTowerService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

@Log4j2
@Getter
@Setter
public class AirportServer {

    private final static String USER = "postgres";
    private final static String PASSWORD = "root";
    private final static String DATABASE = "airport_system";
    private final static String URL = String.format("jdbc:postgresql://localhost:%d/%s", 5432, DATABASE);

    private CollisionRepository collisionRepository;
    private PlaneRepository planeRepository;
    private ControlTowerService controlTowerService;
    private boolean running;
    private boolean paused;
    private Instant startTime;

    public AirportServer(
            CollisionRepository collisionRepository,
            PlaneRepository planeRepository,
            ControlTowerService controlTowerService
    ) throws SQLException {
        this.collisionRepository = collisionRepository;
        this.controlTowerService = controlTowerService;
        this.planeRepository = planeRepository;

        this.running = false;
        this.paused = false;
    }

    public void startServer(ServerSocket serverSocket) throws IOException {
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

                Socket clientSocket = serverSocket.accept();
                log.debug("Server connected with client at port: {}", serverSocket.getLocalPort());
                running = true;
                new PlaneHandler(clientSocket, controlTowerService).start();
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
        return Duration.between(getStartTime(), Instant.now());
    }

    public static void main(String[] args) throws IOException, SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            AirportDatabase database = new AirportDatabase(connection);
            PlaneRepository planeRepository = database.getPlaneRepository();
            CollisionRepository collisionRepository = database.getCollisionRepository();

            ControlTowerService controlTowerService = new ControlTowerService(planeRepository);
            AirportServer airportServer = new AirportServer(collisionRepository, planeRepository, controlTowerService);

            try (ServerSocket serverSocket = new ServerSocket(5000)) {
                CollisionService collisionService = new CollisionService(controlTowerService, collisionRepository);
                collisionService.start();

                airportServer.startServer(serverSocket);
            }

        }
    }
}

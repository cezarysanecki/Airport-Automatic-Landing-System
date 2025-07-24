package com.jakub.bone.runners;

import com.jakub.bone.application.PlaneHandler;
import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.service.CollisionService;
import com.jakub.bone.service.ControlTowerService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

@Log4j2
@Getter
@Setter
public class AirportServer {

    private ServerSocket serverSocket;
    private AirportDatabase database;
    private ControlTowerService controlTowerService;
    private boolean running;
    private boolean paused;
    private Instant startTime;

    public AirportServer() throws SQLException {
        this.database = new AirportDatabase();
        this.controlTowerService = new ControlTowerService(database);
        this.running = false;
        this.paused = false;
    }

    public void startServer(int port) throws IOException {
        ThreadContext.put("type", "Server");
        running = true;
        try {
            this.serverSocket = new ServerSocket(port);
            this.startTime = Instant.now();
            log.info("Server started");

            new CollisionService(controlTowerService).start();

            log.info("Collision detector started");

            while (true) {
                if (paused) {
                    Thread.sleep(2000);
                    log.info("Airport paused. Waiting...");
                    continue;
                }

                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientSocket != null) {
                        log.debug("Server connected with client at port: {}", port);
                        running = true;
                        new PlaneHandler(clientSocket, controlTowerService).start();
                    }
                } catch (Exception ex) {
                    if (serverSocket.isClosed()) {
                        return;
                    }
                    log.error("Error handling client connection: {}", ex.getMessage(), ex);
                }
            }
        } catch (IOException ex) {
            log.error("Failed to start AirportServer on port {}: {}", port, ex.getMessage(), ex);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (database != null) {
                database.closeConnection();
                log.info("Database closed successfully");
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                log.info("Server closed successfully");
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing server socket: {}", ex.getMessage(), ex);
        }
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
        AirportServer airportServer = new AirportServer();
        airportServer.startServer(5000);
    }
}

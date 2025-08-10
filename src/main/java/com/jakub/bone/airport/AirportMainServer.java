package com.jakub.bone.airport;

import com.jakub.bone.airport.plane.PlaneServerClient;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Log4j2
public class AirportMainServer {

    private final PlanesRadar planesRadar;

    private boolean running;
    private boolean paused;
    private Instant startTime;

    public AirportMainServer(PlanesRadar planesRadar) {
        this.planesRadar = planesRadar;

        this.running = false;
        this.paused = false;
    }

    public void startServer(ServerSocket serverSocket) throws IOException {
        ThreadContext.put("type", "Server");
        running = true;

        try {
            log.info("Airport server started on port: {}", serverSocket.getLocalPort());
            startTime = Instant.now();

            while (running) {
                if (paused) {
                    log.info("Airport server is paused");
                    Thread.sleep(2000);
                    continue;
                }

                Socket clientSocket = serverSocket.accept();
                log.info("Server accepted connection from: {}", clientSocket.getRemoteSocketAddress());

                PlaneServerClient planeServerClient = new PlaneServerClient(
                        clientSocket,
                        planesRadar
                );

                planeServerClient.start();
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
        paused = true;
    }

    public void resumeServer() {
        paused = false;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public Optional<Duration> getUptime() {
        return Optional.ofNullable(startTime)
                .map(s -> Duration.between(s, Instant.now()));
    }

}

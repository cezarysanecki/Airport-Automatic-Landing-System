package com.jakub.bone.runners;

import com.jakub.bone.plane.server.PlaneHandlerServer;
import com.jakub.bone.plane.server.PlanePhaseProcessorServer;
import com.jakub.bone.service.PlanesRadar;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Log4j2
public class AirportServer {

    private final PlanesRadar planesRadar;

    private boolean running;
    private boolean paused;
    private Instant startTime;

    public AirportServer(PlanesRadar planesRadar) {
        this.planesRadar = planesRadar;

        this.running = false;
        this.paused = false;
    }

    public void startServer(ServerSocket serverSocket) throws IOException {
        ThreadContext.put("type", "Server");
        running = true;

        try {
            log.info("Server started");
            startTime = Instant.now();

            while (running) {
                if (paused) {
                    log.info("Airport paused. Waiting...");
                    Thread.sleep(2000);
                    continue;
                }

                log.debug("Server connected with client at port: {}", serverSocket.getLocalPort());

                PlanePhaseProcessorServer planePhaseProcessorServer = new PlanePhaseProcessorServer(planesRadar);
                PlaneHandlerServer planeHandlerServer = new PlaneHandlerServer(
                        serverSocket,
                        planesRadar,
                        planePhaseProcessorServer
                );

                planeHandlerServer.start();
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

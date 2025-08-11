package com.jakub.bone.runners;

import com.jakub.bone.config.Constant;
import com.jakub.bone.config.ServerConstants;
import com.jakub.bone.plane.PlaneClient;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class PlaneClientRunner {

    private static final int NUMBER_OF_CLIENTS = 100;
    private static final int CLIENT_SPAWN_DELAY = Constant.CLIENT_SPAWN_DELAY;

    public static void main(String[] args) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CLIENTS)) {
            for (int i = 0; i < NUMBER_OF_CLIENTS; i++) {
                PlaneClient client;
                try {
                    client = new PlaneClient(ServerConstants.IP, ServerConstants.PORT);
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to create PlaneClient due to I/O issues", ex);
                }

                try {
                    Thread.sleep(CLIENT_SPAWN_DELAY);
                } catch (InterruptedException ex) {
                    log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                    Thread.currentThread().interrupt();
                }

                executorService.execute(client);
            }
        }
    }

}
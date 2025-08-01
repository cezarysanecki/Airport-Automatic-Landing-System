package com.jakub.bone.infrastructure;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Log4j2
public class SocketClient {

    private final String ip;
    private final int port;

    private Socket socket;

    @Getter
    private ObjectOutputStream out;
    @Getter
    private ObjectInputStream in;

    public SocketClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startConnection() {
        try {
            this.socket = new Socket(ip, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            log.info("Connection established successfully, {}", socket.toString());
        } catch (IOException ex) {
            log.error("Failed to connect to server at {}:{} - {}", ip, port, ex.getMessage(), ex);
        }
    }

    public void stopConnection() {
        closeResources(out, in);
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ex) {
                log.error("Failed to close socket: {}", ex.getMessage(), ex);
            }
        }
    }

    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ex) {
                    log.error("Failed to close resource: {}", ex.getMessage(), ex);
                }
            }
        }
    }
}

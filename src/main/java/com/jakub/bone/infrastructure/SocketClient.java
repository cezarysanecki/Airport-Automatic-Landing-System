package com.jakub.bone.infrastructure;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Log4j2
public class SocketClient {

    private final Socket socket;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private SocketClient(Socket socket) {
        this.socket = socket;
    }

    public static SocketClient create(String ip, int port) throws IOException {
        return new SocketClient(new Socket(ip, port));
    }

    public static SocketClient create(Socket socket) {
        return new SocketClient(socket);
    }

    public void startConnection() {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            log.info("Connected to server at {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
        } catch (IOException ex) {
            log.error("Failed to start connection: {}", ex.getMessage(), ex);
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

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}

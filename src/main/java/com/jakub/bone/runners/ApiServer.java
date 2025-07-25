package com.jakub.bone.runners;

import com.jakub.bone.config.DbConstants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class ApiServer {

    public static void main(String[] args) {
        Server server = new Server(8080);

        try (Connection connection = DriverManager.getConnection(DbConstants.URL, DbConstants.USER, DbConstants.PASSWORD)) {
            ServletContextHandler context = new AirportServerContext(connection);

            server.setHandler(context);

            server.start();
            System.out.println("Server is running on http://localhost:8080");
            try {
                server.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("API Server interrupted: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.err.println("Failed to start API Server: " + ex.getMessage());
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                System.err.println("Failed to stop API Server: " + e.getMessage());
            }
        }
    }
}

package com.jakub.bone.api.post;

import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/stop")
public class StopAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        try {
            if (airportServer == null && !airportServer.isRunning()) {
                messenger.send(response, Map.of("message", "airport is not running"));
                return;
            }
            airportServer.stopServer();
        } catch (Exception ex) {
            messenger.send(response, Map.of("error", "Failed to stop airport"));
            System.err.println("Error stopping airport: " + ex.getMessage());
        }
        messenger.send(response, Map.of("message", "airport stopped successfully"));
    }
}

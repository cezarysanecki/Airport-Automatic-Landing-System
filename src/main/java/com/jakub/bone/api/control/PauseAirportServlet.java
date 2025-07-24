package com.jakub.bone.api.control;

import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.utils.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/pause")
public class PauseAirportServlet extends HttpServlet {

    private AirportServer airportServer;
    private Messenger messenger;

    @Override
    public void init() throws ServletException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        this.messenger = new Messenger();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (airportServer.isPaused()) {
                messenger.send(response, Map.of("message", "airport is already paused"));
            } else {
                airportServer.pauseServer();
                messenger.send(response, Map.of("message", "airport paused successfully"));
            }
        } catch (Exception ex) {
            messenger.send(response, Map.of("error", "Failed to pause airport"));
            System.err.println("Error pausing airport: " + ex.getMessage());
        }
    }
}

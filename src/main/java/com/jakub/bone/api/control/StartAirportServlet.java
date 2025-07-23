package com.jakub.bone.api.control;

import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.utils.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/start")
public class StartAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private AirportStateService airportStateService;
    private Messenger messenger;

    @Override
    public void init() throws ServletException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        this.airportStateService = new AirportStateService(airportServer);
        this.messenger = new Messenger();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (airportServer.isRunning()) {
                messenger.send(response, Map.of("message", "airport is already running"));
            } else {
                airportStateService.startAirport();
                messenger.send(response, Map.of("message", "airport started successfully"));
            }
        } catch (Exception ex){
            messenger.send(response, Map.of("error", "Failed to start airport"));
            System.err.println("Error starting airport: " + ex.getMessage());
        }
    }
}
package com.jakub.bone.api.control;

import com.jakub.bone.api.JsonSender;
import com.jakub.bone.config.ServerConstants;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.runners.AirportServerFactory;
import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.service.PlanesRadar;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/start")
public class StartAirportServlet extends HttpServlet {

    private AirportServer airportServer;
    private AirportStateService airportStateService;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        AirportServerFactory airportServerFactory = (AirportServerFactory) servletContext.getAttribute("airportServerFactory");

        this.airportServer = airportServerFactory.airportServer;

        PlanesRadar planesRadar = airportServerFactory.planesRadar;
        CollisionRepository collisionRepository = airportServerFactory.collisionRepository;

        this.airportStateService = new AirportStateService(airportServer, planesRadar, collisionRepository);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (airportServer.isRunning()) {
                JsonSender.responseWithJson(response, Map.of("message", "airport is already running"));
            } else {
                try (ServerSocket serverSocket = new ServerSocket(ServerConstants.PORT)) {
                    airportStateService.startAirport(serverSocket);
                    JsonSender.responseWithJson(response, Map.of("message", "airport started successfully"));
                }
            }
        } catch (Exception ex) {
            JsonSender.responseWithJson(response, Map.of("error", "Failed to start airport"));
            System.err.println("Error starting airport: " + ex.getMessage());
        }
    }
}
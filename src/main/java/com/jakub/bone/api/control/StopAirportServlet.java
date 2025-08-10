package com.jakub.bone.api.control;

import com.jakub.bone.api.JsonSender;
import com.jakub.bone.airport.AirportMainServer;
import com.jakub.bone.runners.AirportServerFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/stop")
public class StopAirportServlet extends HttpServlet {

    private AirportMainServer airportMainServer;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        AirportServerFactory airportServerFactory = (AirportServerFactory) servletContext.getAttribute("airportServerFactory");

        this.airportMainServer = airportServerFactory.airportMainServer;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!airportMainServer.isRunning()) {
                JsonSender.responseWithJson(response, Map.of("message", "airport is not running"));
                return;
            }
            airportMainServer.stopServer();
        } catch (Exception ex) {
            JsonSender.responseWithJson(response, Map.of("error", "Failed to stop airport"));
            System.err.println("Error stopping airport: " + ex.getMessage());
        }
        JsonSender.responseWithJson(response, Map.of("message", "airport stopped successfully"));
    }
}

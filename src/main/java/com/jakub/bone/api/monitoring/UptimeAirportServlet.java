package com.jakub.bone.api.monitoring;

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

@WebServlet(urlPatterns = "/airport/uptime")
public class UptimeAirportServlet extends HttpServlet {

    private AirportMainServer airportMainServer;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        AirportServerFactory airportServerFactory = (AirportServerFactory) servletContext.getAttribute("airportServerFactory");

        this.airportMainServer = airportServerFactory.airportMainServer;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            airportMainServer.getUptime()
                    .ifPresentOrElse(uptime -> {
                                long hours = uptime.toHours();
                                long minutes = uptime.toMinutes() % 60;
                                long seconds = uptime.getSeconds() % 60;

                                try {
                                    JsonSender.responseWithJson(response, Map.of("message", String.format("%02d:%02d:%02d", hours, minutes, seconds)));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            () -> {
                                try {
                                    JsonSender.responseWithJson(response, Map.of("message", "airport is not running"));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
        } catch (Exception ex) {
            JsonSender.responseWithJson(response, Map.of("error", "Failed to retrieve uptime"));
            System.err.println("Error retrieving update data: " + ex.getMessage());
        }
    }
}

package com.jakub.bone.api.monitoring;

import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.utils.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/uptime")
public class UptimeAirportServlet extends HttpServlet {

    private AirportServer airportServer;
    private Messenger messenger;

    @Override
    public void init() throws ServletException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        this.messenger = new Messenger();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (airportServer.getStartTime() == null) {
                messenger.send(response, Map.of("message", "airport is not running"));
                return;
            }

            long hours = airportServer.getUptime().toHours();
            long minutes = airportServer.getUptime().toMinutes() % 60;
            long seconds = airportServer.getUptime().getSeconds() % 60;

            messenger.send(response, Map.of("message", String.format("%02d:%02d:%02d", hours, minutes, seconds)));
        } catch (Exception ex) {
            messenger.send(response, Map.of("error", "Failed to retrieve uptime"));
            System.err.println("Error retrieving update data: " + ex.getMessage());
        }
    }
}

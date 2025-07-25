package com.jakub.bone.api.control;

import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.runners.AirportServerContext;
import com.jakub.bone.utils.Messenger;
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
    private Messenger messenger;

    @Override
    public void init() throws ServletException {
        AirportServerContext servletContext = (AirportServerContext) getServletContext();

        this.airportServer = servletContext.airportServerFactory.airportServer;
        this.messenger = new Messenger();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!airportServer.isRunning()) {
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

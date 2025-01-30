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

@WebServlet(urlPatterns = "/airport/resume")
public class ResumeAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        try {
            if (airportServer != null && airportServer.isPaused()) {
                airportServer.resumeServer();
            }
        } catch (Exception ex) {
            messenger.send(response, Map.of("error", "Failed to resume airport"));
            System.err.println("Error resuming airport: " + ex.getMessage());
        }
        messenger.send(response, Map.of("message", "airport resumed successfully"));
    }
}

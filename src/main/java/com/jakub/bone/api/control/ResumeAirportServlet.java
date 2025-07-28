package com.jakub.bone.api.control;

import com.jakub.bone.api.JsonSender;
import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.runners.AirportServerFactory;
import jakarta.servlet.ServletContext;
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

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        AirportServerFactory airportServerFactory = (AirportServerFactory) servletContext.getAttribute("airportServerFactory");

        this.airportServer = airportServerFactory.airportServer;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (airportServer.isPaused()) {
                airportServer.resumeServer();
            }
        } catch (Exception ex) {
            JsonSender.responseWithJson(response, Map.of("error", "Failed to resume airport"));
            System.err.println("Error resuming airport: " + ex.getMessage());
        }
        JsonSender.responseWithJson(response, Map.of("message", "airport resumed successfully"));
    }
}

package com.jakub.bone.api.monitoring;

import com.jakub.bone.runners.AirportServer;
import com.jakub.bone.utils.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/collisions")
public class CollisionsAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger;

    @Override
    public void init() throws ServletException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        this.messenger = new Messenger();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> collidedPlanes = airportServer.getDatabase().getCOLLISION_REPOSITORY().getCollidedPlanes();
        messenger.send(response, Map.of("collided planes", collidedPlanes));
    }
}

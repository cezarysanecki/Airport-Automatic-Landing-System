package com.jakub.bone.api.monitoring;

import com.jakub.bone.api.JsonSender;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.runners.AirportServerFactory;
import jakarta.servlet.ServletContext;
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

    private CollisionRepository collisionRepository;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        AirportServerFactory airportServerFactory = (AirportServerFactory) servletContext.getAttribute("airportServerFactory");

        this.collisionRepository = airportServerFactory.collisionRepository;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> collidedPlanes = collisionRepository.getCollidedPlanes();
        JsonSender.responseWithJson(response, Map.of("collided planes", collidedPlanes));
    }
}

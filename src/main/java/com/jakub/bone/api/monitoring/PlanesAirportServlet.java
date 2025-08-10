package com.jakub.bone.api.monitoring;

import com.jakub.bone.api.JsonSender;
import com.jakub.bone.domain.plane.PlaneNumber;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.runners.AirportServerFactory;
import com.jakub.bone.airport.PlanesRadar;
import com.jakub.bone.airport.dto.PlaneCoordinates;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/planes/*")
public class PlanesAirportServlet extends HttpServlet {

    private PlaneRepository planeRepository;
    private PlanesRadar planesRadar;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        AirportServerFactory airportServerFactory = (AirportServerFactory) servletContext.getAttribute("airportServerFactory");

        this.planeRepository = airportServerFactory.planeRepository;
        this.planesRadar = airportServerFactory.planesRadar;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String path = request.getPathInfo();
            switch (path) {
                case "/count" -> {
                    int planesCount = planesRadar.countPlanes();
                    JsonSender.responseWithJson(response, Map.of("count", planesCount));
                }
                case "/flightNumbers" -> {
                    List<String> flightNumbers = planesRadar.getAllFlightNumbers();
                    JsonSender.responseWithJson(response, Map.of("flight numbers", flightNumbers));
                }
                case "/landed" -> {
                    List<String> landedPlanes = planeRepository.getLandedPlanes();
                    JsonSender.responseWithJson(response, Map.of("landed planes", landedPlanes));
                }
                default -> {
                    String flightNumber = path.substring(1);
                    PlaneCoordinates planeCoordinates = planesRadar.getPlaneByFlightNumber(new PlaneNumber(flightNumber));

                    if (planeCoordinates == null) {
                        JsonSender.responseWithJson(response, Map.of("message", "plane not found"));
                    } else {
                        JsonSender.responseWithJson(response, PlanesMapper.toMap(planeCoordinates));
                    }
                }
            }
        } catch (Exception ex) {
            JsonSender.responseWithJson(response, Map.of("error", "Internal server error"));
            System.err.println("Error handling request: " + ex.getMessage());
        }
    }
}

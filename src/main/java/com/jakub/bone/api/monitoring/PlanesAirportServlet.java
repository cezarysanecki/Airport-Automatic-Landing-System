package com.jakub.bone.api.monitoring;

import com.jakub.bone.api.JsonSender;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.runners.AirportServerFactory;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.utils.Messenger;
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
    private ControlTowerService controlTowerService;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        AirportServerFactory airportServerFactory = (AirportServerFactory) servletContext.getAttribute("airportServerFactory");

        this.planeRepository = airportServerFactory.planeRepository;
        this.controlTowerService = airportServerFactory.controlTowerService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String path = request.getPathInfo();
            switch (path) {
                case "/count" -> {
                    int planesCount = controlTowerService.countPlanes();
                    JsonSender.responseWithJson(response, Map.of("count", planesCount));
                }
                case "/flightNumbers" -> {
                    List<String> flightNumbers = controlTowerService.getAllFlightNumbers();
                    JsonSender.responseWithJson(response, Map.of("flight numbers", flightNumbers));
                }
                case "/landed" -> {
                    List<String> landedPlanes = planeRepository.getLandedPlanes();
                    JsonSender.responseWithJson(response, Map.of("landed planes", landedPlanes));
                }
                default -> {
                    String flightNumber = path.substring(1);
                    Plane plane = controlTowerService.getPlaneByFlightNumber(flightNumber);

                    if (plane == null) {
                        JsonSender.responseWithJson(response, Map.of("message", "plane not found"));
                    } else {
                        JsonSender.responseWithJson(response, PlanesMapper.toMap(plane));
                    }
                }
            }
        } catch (Exception ex) {
            JsonSender.responseWithJson(response, Map.of("error", "Internal server error"));
            System.err.println("Error handling request: " + ex.getMessage());
        }
    }
}

package com.jakub.bone.ui.utills;

import com.jakub.bone.config.Constant;
import com.jakub.bone.domain.airport.Coordinates;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.ui.model.PlaneModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jakub.bone.domain.plane.Plane.FlightPhase.LANDING;

@Log4j2
public class SceneUpdater {
    private final Group root;
    private final ControlTowerService controller;
    private final Map<String, PlaneModel> planesMap;
    private boolean isFirstPlane;

    public SceneUpdater(Group root, ControlTowerService controller) {
        this.root = root;
        this.controller = controller;
        this.planesMap = new HashMap<>();
        this.isFirstPlane = true;
    }

    public void start() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateAirspace() {
        List<Plane> planes = controller.getPlanes();

        for (Plane plane : planes) {
            if (isFirstPlane) {
                try {
                    Thread.sleep(Constant.SCENE_UPDATE_DELAY);
                } catch (InterruptedException ex) {
                    log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                    Thread.currentThread().interrupt();
                }
                isFirstPlane = false;
            }
            PlaneModel planeModel;
            if (!planesMap.containsKey(plane.getFlightNumber())) {
                planeModel = new PlaneModel(plane);
                planesMap.put(plane.getFlightNumber(), planeModel);
                root.getChildren().addAll(planeModel.getPlaneGroup(), planeModel.getLabel());
            }

            planeModel = planesMap.get(plane.getFlightNumber());
            if (plane.getPhase().equals(LANDING)) {
                planeModel.setPlaneModelColor(Color.YELLOW);
            }

            Coordinates nextWaypoint = plane.getNavigator().getCoordinates();
            planeModel.animateMovement(nextWaypoint);
        }
        cleanupScene();
    }

    private void cleanupScene() {
        for (String flightNumber : planesMap.keySet()) {
            Plane plane = controller.getPlaneByFlightNumber(flightNumber);
            PlaneModel planeModel = planesMap.get(flightNumber);
            if (plane == null || plane.isDestroyed() || plane.isLanded()) {
                root.getChildren().removeAll(planeModel.getPlaneGroup(), planeModel.getLabel());
            }
        }
    }
}


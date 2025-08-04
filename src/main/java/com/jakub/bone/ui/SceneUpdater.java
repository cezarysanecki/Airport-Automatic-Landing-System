package com.jakub.bone.ui;

import com.jakub.bone.config.Constant;
import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.service.PlanesRadar;
import com.jakub.bone.service.PlaneCoordinates;
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

@Log4j2
class SceneUpdater {

    private final Group root;
    private final PlanesRadar controller;
    private final Map<String, PlaneModel> planesMap;
    private boolean isFirstPlane;

    SceneUpdater(Group root, PlanesRadar controller) {
        this.root = root;
        this.controller = controller;
        this.planesMap = new HashMap<>();
        this.isFirstPlane = true;
    }

    void start() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateAirspace() {
        List<PlaneCoordinates> planesCoordinates = controller.getPlaneCoordinates();

        for (PlaneCoordinates planeCoordinates : planesCoordinates) {
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
            if (!planesMap.containsKey(planeCoordinates.flightNumber())) {
                planeModel = new PlaneModel(planeCoordinates);
                planesMap.put(planeCoordinates.flightNumber(), planeModel);
                root.getChildren().addAll(planeModel.getPlaneGroup(), planeModel.getLabel());
            }

            planeModel = planesMap.get(planeCoordinates.flightNumber());
            if (planeCoordinates.landing()) {
                planeModel.setPlaneModelColor(Color.YELLOW);
            }

            Coordinates nextWaypoint = planeCoordinates.coordinates();
            planeModel.animateMovement(nextWaypoint);
        }
        cleanupScene();
    }

    private void cleanupScene() {
        for (String flightNumber : planesMap.keySet()) {
            PlaneModel planeModel = planesMap.get(flightNumber);
            boolean isPresent = controller.isPlanePresent(flightNumber);
            if (!isPresent) {
                root.getChildren().removeAll(planeModel.getPlaneGroup(), planeModel.getLabel());
            }
        }
    }
}


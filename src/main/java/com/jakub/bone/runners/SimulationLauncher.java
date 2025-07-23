package com.jakub.bone.runners;

import com.jakub.bone.service.AirportStateService;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SimulationLauncher extends Application {
    private AirportStateService airportStateService;
    private SceneRenderer visualization;

    @Override
    public void init() throws Exception {
        AirportServer airportServer = new AirportServer();
        this.airportStateService = new AirportStateService(airportServer);
        this.visualization = new SceneRenderer(airportServer.getControlTowerService());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        airportStateService.startAirport();
        visualization.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



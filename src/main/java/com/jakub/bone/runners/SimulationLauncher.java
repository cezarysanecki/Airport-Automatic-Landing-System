package com.jakub.bone.runners;

import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.service.ControlTowerService;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SimulationLauncher extends Application {

    private AirportStateService airportStateService;
    private SceneRenderer visualization;

    @Override
    public void init() throws Exception {
        AirportDatabase database = new AirportDatabase();
        ControlTowerService controlTowerService = new ControlTowerService(database);

        AirportServer airportServer = new AirportServer(database, controlTowerService);

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



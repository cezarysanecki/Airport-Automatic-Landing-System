package com.jakub.bone.runners;

import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.service.ControlTowerService;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;

@Log4j2
public class SimulationLauncher extends Application {

    private final static String USER = "postgres";
    private final static String PASSWORD = "root";
    private final static String DATABASE = "airport_system";
    private final static String URL = String.format("jdbc:postgresql://localhost:%d/%s", 5432, DATABASE);

    private Connection connection;
    private ServerSocket serverSocket;
    private AirportStateService airportStateService;
    private SceneRenderer visualization;

    @Override
    public void init() throws Exception {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);

        AirportDatabase database = new AirportDatabase(connection);
        PlaneRepository planeRepository = database.getPlaneRepository();
        CollisionRepository collisionRepository = database.getCollisionRepository();

        ControlTowerService controlTowerService = new ControlTowerService(planeRepository);
        AirportServer airportServer = new AirportServer(collisionRepository, planeRepository, controlTowerService);

        this.airportStateService = new AirportStateService(airportServer, controlTowerService, collisionRepository);
        this.visualization = new SceneRenderer(airportServer.getControlTowerService());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        serverSocket = new ServerSocket(5000);
        airportStateService.startAirport(serverSocket);
        visualization.start(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        connection.close();
        serverSocket.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



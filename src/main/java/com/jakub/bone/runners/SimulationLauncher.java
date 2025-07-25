package com.jakub.bone.runners;

import com.jakub.bone.config.DbConstants;
import com.jakub.bone.config.ServerConstants;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.service.ControlTowerService;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.server.Server;

import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;

@Log4j2
public class SimulationLauncher extends Application {

    private Connection connection;
    private ServerSocket serverSocket;
    private AirportStateService airportStateService;
    private SceneRenderer visualization;
    private Server server;

    @Override
    public void init() throws Exception {
        this.connection = DriverManager.getConnection(DbConstants.URL, DbConstants.USER, DbConstants.PASSWORD);
        this.server = new Server(8080);
        AirportServerFactory airportServerFactory = new AirportServerFactory(connection);

        AirportServer airportServer = airportServerFactory.airportServer;
        CollisionRepository collisionRepository = airportServerFactory.collisionRepository;
        ControlTowerService controlTowerService = airportServerFactory.controlTowerService;

        this.airportStateService = new AirportStateService(airportServer, controlTowerService, collisionRepository);
        this.visualization = new SceneRenderer(controlTowerService);

        ApiServerRunner.run(connection, server);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        serverSocket = new ServerSocket(ServerConstants.PORT);
        airportStateService.startAirport(serverSocket);
        visualization.start(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        connection.close();
        serverSocket.close();
        server.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



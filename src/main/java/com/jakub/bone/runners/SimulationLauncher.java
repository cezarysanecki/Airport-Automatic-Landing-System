package com.jakub.bone.runners;

import com.jakub.bone.airport.AirportMainServer;
import com.jakub.bone.config.DbConstants;
import com.jakub.bone.config.ServerConstants;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.airport.AirportStateService;
import com.jakub.bone.airport.PlanesRadar;
import com.jakub.bone.ui.SceneRenderer;
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

        AirportMainServer airportMainServer = airportServerFactory.airportMainServer;
        CollisionRepository collisionRepository = airportServerFactory.collisionRepository;
        PlanesRadar planesRadar = airportServerFactory.planesRadar;

        this.airportStateService = new AirportStateService(airportMainServer, planesRadar, collisionRepository);
        this.visualization = new SceneRenderer(planesRadar);

        ApiServerRunner.run(server, airportServerFactory);
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



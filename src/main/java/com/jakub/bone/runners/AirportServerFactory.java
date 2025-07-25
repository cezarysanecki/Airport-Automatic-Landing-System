package com.jakub.bone.runners;

import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.service.ControlTowerService;

import java.sql.Connection;
import java.sql.SQLException;

public class AirportServerFactory {

    public final AirportServer airportServer;
    public final ControlTowerService controlTowerService;
    public final CollisionRepository collisionRepository;
    public final PlaneRepository planeRepository;

    public AirportServerFactory(Connection dbConnection) throws SQLException {
        AirportDatabase database = new AirportDatabase(dbConnection);
        PlaneRepository planeRepository = database.getPlaneRepository();

        this.collisionRepository = database.getCollisionRepository();
        this.planeRepository = database.getPlaneRepository();
        this.controlTowerService = new ControlTowerService(planeRepository);
        this.airportServer = new AirportServer(controlTowerService);
    }

}

package com.jakub.bone.runners;

import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.service.PlanesRadar;

import java.sql.Connection;
import java.sql.SQLException;

public class AirportServerFactory {

    public final AirportServer airportServer;
    public final PlanesRadar planesRadar;
    public final CollisionRepository collisionRepository;
    public final PlaneRepository planeRepository;

    public AirportServerFactory(Connection dbConnection) throws SQLException {
        AirportDatabase database = new AirportDatabase(dbConnection);

        this.collisionRepository = database.getCollisionRepository();
        this.planeRepository = database.getPlaneRepository();
        this.planesRadar = new PlanesRadar();
        this.airportServer = new AirportServer(planesRadar);
    }

}

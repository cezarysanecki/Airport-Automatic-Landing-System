package com.jakub.bone.runners;

import com.jakub.bone.airport.AirportMainServer;
import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.airport.PlanesRadar;

import java.sql.Connection;
import java.sql.SQLException;

public class AirportServerFactory {

    public final AirportMainServer airportMainServer;
    public final PlanesRadar planesRadar;
    public final CollisionRepository collisionRepository;
    public final PlaneRepository planeRepository;

    public AirportServerFactory(Connection dbConnection) throws SQLException {
        AirportDatabase database = new AirportDatabase(dbConnection);

        this.collisionRepository = database.getCollisionRepository();
        this.planeRepository = database.getPlaneRepository();
        this.planesRadar = new PlanesRadar();
        this.airportMainServer = new AirportMainServer(planesRadar);
    }

}

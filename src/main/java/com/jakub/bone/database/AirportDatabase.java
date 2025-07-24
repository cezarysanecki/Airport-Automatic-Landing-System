package com.jakub.bone.database;

import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
@Log4j2
public class AirportDatabase {

    private final PlaneRepository planeRepository;
    private final CollisionRepository collisionRepository;
    private final Connection connection;

    public AirportDatabase(Connection connection) throws SQLException {
        this.connection = connection;

        DSLContext context = DSL.using(connection);
        this.planeRepository = new PlaneRepository(context);
        this.collisionRepository = new CollisionRepository(context);
    }

}

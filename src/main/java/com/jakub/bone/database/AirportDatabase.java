package com.jakub.bone.database;

import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@Log4j2
public class AirportDatabase {

    private final static String USER = "postgres";
    private final static String PASSWORD = "root";
    private final static String DATABASE = "airport_system";
    private final static String URL = String.format("jdbc:postgresql://localhost:%d/%s", 5432, DATABASE);

    private final PlaneRepository planeRepository;
    private final CollisionRepository collisionRepository;
    private final Connection connection;

    public AirportDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        this.connection = connection;

        DSLContext context = DSL.using(connection);
        this.planeRepository = new PlaneRepository(context);
        this.collisionRepository = new CollisionRepository(context);
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.info("Connection to database '{}' closed successfully.", DATABASE);
            } catch (SQLException ex) {
                log.error("Failed to close connection. Error: {}", ex.getMessage(), ex);
            }
        }
    }
}

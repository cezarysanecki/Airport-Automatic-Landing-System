package com.jakub.bone.database;

import com.jakub.bone.repository.DatabaseSchema;
import com.jakub.bone.repository.PlaneRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import com.jakub.bone.repository.CollisionRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@Log4j2
public class AirportDatabase {
    private final String USER = "postgres";
    private final String PASSWORD = "root";
    private final String DATABASE = "airport_system";
    private final String URL = String.format("jdbc:postgresql://localhost:%d/%s", 5432, DATABASE);;
    private final DSLContext CONTEXT;
    private final DatabaseSchema SCHEMA;
    private final PlaneRepository PLANE_REPOSITORY;
    private final CollisionRepository COLLISION_REPOSITORY;
    private Connection connection;

    public AirportDatabase() throws SQLException {
        this.connection = getDatabaseConnection();
        this.CONTEXT = DSL.using(connection);
        this.SCHEMA = new DatabaseSchema(CONTEXT);
        this.PLANE_REPOSITORY = new PlaneRepository(CONTEXT);
        this.COLLISION_REPOSITORY = new CollisionRepository(CONTEXT);
    }

    public Connection getDatabaseConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            log.info("Connection established successfully with database '{}' on port {}", DATABASE, 5432);
        } catch (SQLException ex) {
            log.error("Failed to establish connection to the database '{}'. Error: {}", DATABASE, ex.getMessage(), ex);
        }
        return connection;
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

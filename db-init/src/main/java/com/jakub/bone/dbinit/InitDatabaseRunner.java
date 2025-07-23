package com.jakub.bone.dbinit;

import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j2
public class InitDatabaseRunner {

    private static final String USER = "postgres";
    private static final String PASSWORD = "root";
    private static final String DATABASE = "airport_system";
    private static final String URL = String.format("jdbc:postgresql://localhost:%d/%s", 5432, DATABASE);

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            DSLContext context = DSL.using(connection);
            DatabaseSchema databaseSchema = new DatabaseSchema(context);
            databaseSchema.createTables();
        } catch (SQLException e) {
            log.info("Connection established successfully with database '{}' on port {}", DATABASE, 5432);
        }
    }

}
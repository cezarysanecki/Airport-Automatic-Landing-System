package com.jakub.bone.dbinit;

import com.jakub.bone.config.DbConstants;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j2
public class InitDatabaseRunner {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DbConstants.URL, DbConstants.USER, DbConstants.PASSWORD)) {
            DSLContext context = DSL.using(connection);
            DatabaseSchema databaseSchema = new DatabaseSchema(context);
            databaseSchema.createTables();
        } catch (SQLException e) {
            log.info("Connection established successfully with database '{}' on port {}", DbConstants.DATABASE, 5432);
        }
    }

}
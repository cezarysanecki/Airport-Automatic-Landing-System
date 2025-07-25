package com.jakub.bone.config;

public class DbConstants {

    public final static String USER = "postgres";
    public final static String PASSWORD = "root";
    public final static String DATABASE = "airport_system";
    public final static int DATABASE_PORT = 5432;
    public final static String URL = String.format("jdbc:postgresql://localhost:%d/%s", DATABASE_PORT, DATABASE);

}

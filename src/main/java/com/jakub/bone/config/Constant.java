package com.jakub.bone.config;

import com.jakub.bone.domain.airport.Coordinates;

public class Constant {
    // Runway properties
    public static final int RUNWAY_WIDTH = ConfigLoader.getInt("runway.width");
    public static final int RUNWAY_HEIGHT = ConfigLoader.getInt("runway.height");

    // Plane properties
    public static final int MAX_CAPACITY = ConfigLoader.getInt("airplane.max-capacity");
    public static final int MAX_ALTITUDE = ConfigLoader.getInt("airplane.max-altitude");
    public static final int MIN_ALTITUDE = ConfigLoader.getInt("airplane.min-altitude");

    // Collision properties
    public static final double ALTITUDE_COLLISION_DISTANCE = ConfigLoader.getDouble("collision.altitude-distance");
    public static final double HORIZONTAL_COLLISION_DISTANCE = ConfigLoader.getDouble("collision.horizontal-distance");

    // Fuel properties
    public static final double CONSUMPTION_PER_HOUR = ConfigLoader.getDouble("fuel.consumption-per-hour");
    public static final double INITIAL_FUEL_LEVEL = ConfigLoader.getDouble("fuel.initial-fuel-level");
    public static final double CONSUMPTION_PER_SECOND = CONSUMPTION_PER_HOUR / 3600.0;

    // Delays
    public static final int UPDATE_DELAY = ConfigLoader.getInt("delays.update");
    public static final int SERVER_INIT_DELAY = ConfigLoader.getInt("delays.server-init");
    public static final int COLLISION_CHECK_DELAY = ConfigLoader.getInt("delays.collision-check");
    public static final int AFTER_COLLISION_DELAY = ConfigLoader.getInt("delays.after-collision");
    public static final int LANDING_CHECK_DELAY = ConfigLoader.getInt("delays.landing-check");
    public static final int SCENE_UPDATE_DELAY = ConfigLoader.getInt("delays.scene-update");
    public static final int CLIENT_SPAWN_DELAY = ConfigLoader.getInt("delays.client-spawn");

    // Landing properties
    public static final int LANDING_ALTITUDE = ConfigLoader.getInt("landing.altitude");
    public static final Coordinates LANDING_POINT_RUNWAY_1 = new Coordinates(
            ConfigLoader.getInt("landing.point.runway-1.x"),
            ConfigLoader.getInt("landing.point.runway-1.y"),
            ConfigLoader.getInt("landing.point.runway-1.altitude")
    );
    public static final Coordinates LANDING_POINT_RUNWAY_2 = new Coordinates(
            ConfigLoader.getInt("landing.point.runway-2.x"),
            ConfigLoader.getInt("landing.point.runway-2.y"),
            ConfigLoader.getInt("landing.point.runway-2.altitude")
    );

    public static class Corridor {
        public static Coordinates ENTRY_POINT_CORRIDOR_1 = new Coordinates(
                ConfigLoader.getInt("landing.entry.corridor-1.x"),
                ConfigLoader.getInt("landing.entry.corridor-1.y"),
                ConfigLoader.getInt("landing.entry.corridor-1.altitude"));
        public static Coordinates FINAL_APPROACH_CORRIDOR_1 = new Coordinates(
                ConfigLoader.getInt("landing.final.corridor-1.x"),
                ConfigLoader.getInt("landing.final.corridor-1.y"),
                ConfigLoader.getInt("landing.final.corridor-1.altitude"));

        public static Coordinates ENTRY_POINT_CORRIDOR_2 = new Coordinates(
                ConfigLoader.getInt("landing.entry.corridor-2.x"),
                ConfigLoader.getInt("landing.entry.corridor-2.y"),
                ConfigLoader.getInt("landing.entry.corridor-2.altitude"));
        public static Coordinates FINAL_APPROACH_CORRIDOR_2 = new Coordinates(
                ConfigLoader.getInt("landing.final.corridor-2.x"),
                ConfigLoader.getInt("landing.final.corridor-2.y"),
                ConfigLoader.getInt("landing.final.corridor-2.altitude"));
    }

    // Holding properties
    public static int HOLDING_ALTITUDE = ConfigLoader.getInt("holding.altitude");
    public static int HOLDING_ENTRY_ALTITUDE = ConfigLoader.getInt("holding.entry-altitude");

}

package com.jakub.bone.utils;

import com.jakub.bone.domain.airport.Coordinates;
import com.jakub.bone.domain.airport.Runway;

import java.util.ArrayList;
import java.util.List;

import static com.jakub.bone.config.Constant.*;

public class WaypointGenerator  {
    public static List<Coordinates> getDescentWaypoints() {
        List<Coordinates> waypoints = new ArrayList<>();
        int radius = 5000;
        int totalWaypoints = 320;
        double altitudeDecrease = (double) (MAX_ALTITUDE - HOLDING_ALTITUDE) / totalWaypoints;

        // Each 80 points for one lap of circle
        double angleStep = 360.0 / 80;

        double currentAltitude = MAX_ALTITUDE;

        for (int i = 0; i < totalWaypoints; i++) {
            // Convert the angle to radians for math calculations
            // (i % 80) angle resets after every 80 waypoints, completing a full circle (360 degrees)
            // angleStep defines the angular interval between next points
            double radians = Math.toRadians((i % 80) * angleStep);

            // Calculate coordinates based on circular geometry
            int x = (int) (radius * Math.cos(radians));
            int y = (int) (radius * Math.sin(radians));

            waypoints.add(new Coordinates(x, y, (int) Math.round(currentAltitude)));

            currentAltitude -= altitudeDecrease;
        }
        return waypoints;
    }

    public static List<Coordinates> getHoldingPatternWaypoints() {
        List<Coordinates> waypoints = new ArrayList<>();
        int step = 500; // Distance between waypoints in holding pattern way

        // Line going upwards along the y-axis
        for (int y = 0; y <= 3000; y += step) {
            waypoints.add(new Coordinates(5000, y, HOLDING_ALTITUDE));
        }
        // Curve for the top-right corner
        waypoints.addAll(generateArc(3500, 3500, 1500, 0, 90, HOLDING_ALTITUDE, 0));

        // Line going left along the x-axis
        for (int x = 3000; x >= -3000; x -= step) {
            waypoints.add(new Coordinates(x, 5000, HOLDING_ALTITUDE));
        }
        // Curve for the top-left corner
        waypoints.addAll(generateArc(-3500, 3500, 1500, 90, 180, HOLDING_ALTITUDE, 0));

        // Line going downward along the y-axis
        for (int y = 3000; y >= -3000; y -= step) {
            waypoints.add(new Coordinates(-5000, y, HOLDING_ALTITUDE));
        }
        // Curve for the bottom-left corner
        waypoints.addAll(generateArc(-3500, -3500, 1500, 180, 270, HOLDING_ALTITUDE, 0));

        // Line going right along the x-axis
        for (int x = -3000; x <= 3000; x += step) {
            waypoints.add(new Coordinates(x, -5000, HOLDING_ALTITUDE));
        }
        // Curve for the bottom-right corner
        waypoints.addAll(generateArc(3500, -3500, 1500, -90, 0, HOLDING_ALTITUDE, 0));

        // Line to connect rest of way to the start
        for (int y = -3000; y <= -500; y += step) {
            waypoints.add(new Coordinates(5000, y, HOLDING_ALTITUDE));
        }
        return waypoints;
    }

    public static List<Coordinates> generateArc(int centerX, int centerY, int radius, double startAngle, double endAngle, int altitude, int altitudeDecrement) {
        List<Coordinates> arcPoints = new ArrayList<>();
        int waypointsOnArc = 4;
        double angleStep = (endAngle - startAngle) / (waypointsOnArc - 1);

        for (int i = 0; i < waypointsOnArc; i++) {
            // Calculate the angle in radians for the current waypoint
            // startAngle is the initial angle of the arc, and angleStep defines the angular increment between points
            double angle = Math.toRadians(startAngle + i * angleStep);
            // Compute the X and Y coordinate using the formula for a point's position on a circle
            // centerX is the circle's center along the X-axis
            // centerY is the circle's center along the Y-axis
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            arcPoints.add(new Coordinates(x, y, altitude));
            altitude -= altitudeDecrement;
        }
        return arcPoints;
    }

    public static List<Coordinates> getLandingWaypoints(Runway runway) {
        List<Coordinates> waypoints = new ArrayList<>();
        int landingWaypoints = 10;
        int altitude = HOLDING_ALTITUDE;
        int altitudeDecrement = altitude / landingWaypoints;

        int arcCenterY = "R-2".equals(runway.getId()) ? 0 : 3000;

        // Generate the arc that leads planes into the runway's corridor
        List<Coordinates> arc4 = generateArc(-3500, arcCenterY, 1500, 180, 270, altitude, altitudeDecrement);
        for(Coordinates waypoint: arc4){
            waypoints.add(waypoint);
        }

        // Start lowering altitude toward the ground level
        // Calculate the position for the waypoints leading to the runway
        altitude = LANDING_ALTITUDE;
        int landingDescentY = runway.getCorridor().getEntryPoint().y() - 2000;
        for (int x = -3000; x <= 500; x += 500) {
            waypoints.add(new Coordinates(x, landingDescentY, altitude));
            altitude -= altitudeDecrement;
        }

        // Add waypoints directly on the runway stop point
        altitude = 0;
        for (int x = 1000; x <= 3000; x += 250) {
            waypoints.add(new Coordinates(x, runway.getLandingPoint().y(), altitude));
        }
        return waypoints;
    }
}


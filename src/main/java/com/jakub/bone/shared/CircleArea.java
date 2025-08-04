package com.jakub.bone.shared;

public record CircleArea(Coordinates center, double radius) {

    public boolean within(Coordinates coordinates) {
        double dx = coordinates.getX() - center.getX();
        double dy = coordinates.getY() - center.getY();
        double dz = coordinates.getAltitude() - center.getAltitude();
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        return distanceSquared <= radius * radius;
    }
}

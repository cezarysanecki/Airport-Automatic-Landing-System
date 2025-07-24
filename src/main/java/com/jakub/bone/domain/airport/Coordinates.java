package com.jakub.bone.domain.airport;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public final class Coordinates implements Serializable {

    private int x;
    private int y;
    private int altitude;

    public Coordinates(int x, int y, int altitude) {
        this.x = x;
        this.y = y;
        this.altitude = altitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coordinates location = (Coordinates) obj;
        return x == location.x && y == location.y && altitude == location.altitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, altitude);
    }

}
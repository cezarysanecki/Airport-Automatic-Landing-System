package com.jakub.bone.domain.airport;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class Coordinates implements Serializable {
    int x;
    int y;
    int altitude;

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
        Coordinates coordinates = (Coordinates) obj;
        return x == coordinates.x && y == coordinates.y && altitude == coordinates.altitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, altitude);
    }
}
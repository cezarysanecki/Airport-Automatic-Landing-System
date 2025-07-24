package com.jakub.bone.domain.airport;

import java.io.Serializable;

public record Coordinates(
        int x,
        int y,
        int altitude
) implements Serializable {

}
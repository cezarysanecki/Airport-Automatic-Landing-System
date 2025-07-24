package com.jakub.bone.domain.airport;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Corridor implements Serializable {
    private final String id;
    private final Coordinates entryPoint;
    private final Coordinates finalApproachPoint;

    public Corridor(String id, Coordinates entryPoint, Coordinates finalApproachPoint) {
        this.id = id;
        this.entryPoint = entryPoint;
        this.finalApproachPoint = finalApproachPoint;
    }
}

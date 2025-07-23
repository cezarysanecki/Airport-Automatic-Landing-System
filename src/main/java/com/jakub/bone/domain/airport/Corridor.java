package com.jakub.bone.domain.airport;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Corridor implements Serializable {
    private String id;
    private Coordinates entryPoint;
    private Coordinates finalApproachPoint;

    public Corridor(String id, Coordinates entryPoint, Coordinates finalApproachPoint) {
        this.id = id;
        this.entryPoint = entryPoint;
        this.finalApproachPoint = finalApproachPoint;
    }
}

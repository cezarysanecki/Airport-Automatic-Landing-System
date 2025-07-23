package com.jakub.bone.domain.airport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Getter
@Setter
@Builder
public class Runway implements Serializable {
    private String id;
    private Coordinates landingPoint;
    private Corridor corridor;
    private boolean available;

    public Runway(String id, Coordinates landingPoint, Corridor corridor, boolean available) {
        this.id = id;
        this.landingPoint = landingPoint;
        this.corridor = corridor;
        this.available = available;
    }
}

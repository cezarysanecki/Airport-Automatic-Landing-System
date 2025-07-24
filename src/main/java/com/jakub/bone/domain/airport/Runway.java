package com.jakub.bone.domain.airport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Runway implements Serializable {

    private String id;
    private Coordinates landingPoint;
    private Corridor corridor;
    private boolean available;

}

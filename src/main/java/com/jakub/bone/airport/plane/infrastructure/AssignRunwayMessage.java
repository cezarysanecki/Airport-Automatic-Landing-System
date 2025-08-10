package com.jakub.bone.airport.plane.infrastructure;

import com.jakub.bone.domain.airport.Runway;

public class AssignRunwayMessage {

    public Runway runway;

    public AssignRunwayMessage() {
    }

    public AssignRunwayMessage(Runway runway) {
        this.runway = runway;
    }

}

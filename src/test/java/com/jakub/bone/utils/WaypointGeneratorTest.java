package com.jakub.bone.utils;

import com.jakub.bone.domain.airport.Coordinates;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WaypointGeneratorTest {

    @Test
    void test() {
        List<Coordinates> descentWaypoints = WaypointGenerator.getDescentWaypoints();

        assertEquals(320, descentWaypoints.size());
    }

}
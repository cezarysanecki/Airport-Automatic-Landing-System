package com.jakub.bone.utils;

import com.jakub.bone.domain.Coordinates;
import com.jakub.bone.domain.WaypointGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WaypointGeneratorTest {

    @Test
    void generate_specified_number_of_descent_waypoints() {
        List<Coordinates> descentWaypoints = WaypointGenerator.prepareDescendingWaypoints();

        assertEquals(320, descentWaypoints.size());
    }

}
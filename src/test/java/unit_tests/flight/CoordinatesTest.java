package unit_tests.flight;

import com.jakub.bone.domain.airport.Coordinates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.jakub.bone.domain.plane.Plane;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesTest {
    @Test
    @DisplayName("Plane should update its location correctly")
    void testUpdateLocation() {
        Plane plane = new Plane("TEST_PLANE");
        Coordinates initCoordinates = new Coordinates(1000, 1000, 1000);
        Coordinates newCoordinates = new Coordinates(5000, 5000, 5000);

        // Assign initial location
        plane.getNavigator().setCoordinates(initCoordinates);

        // Update location
        plane.getNavigator().updateLocation(newCoordinates);

        assertNotEquals(plane.getNavigator().getCoordinates(), initCoordinates, "Plane should not remain at the initial location");
        assertEquals(plane.getNavigator().getCoordinates(), newCoordinates, "Plane should have the new location after update");
    }

    @Test
    @DisplayName("Location equals() should distinguish different coordinates")
    void testEqualLocationIdentification(){
        // Two planes with different locations
        Plane plane1 = new Plane("TEST_PLANE_1");
        plane1.getNavigator().setCoordinates(new Coordinates(1000, 1000, 1000));

        Plane plane2 = new Plane("TEST_PLANE_2");
        plane1.getNavigator().setCoordinates(new Coordinates(5000, 5000, 5000));
        // Compare the locations
        boolean isLocationEqual = plane1.getNavigator()
                                        .getCoordinates()
                                        .equals(plane2.getNavigator().getCoordinates());

        assertFalse(isLocationEqual, "Locations with different coordinates should not be equal");
    }
}

package unit_tests.flight;

import com.jakub.bone.config.Constant;
import com.jakub.bone.utils.WaypointGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.jakub.bone.domain.plane.Plane;

import static com.jakub.bone.domain.plane.FlightPhase.DESCENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FuelManagerTest {
    @Test
    @DisplayName("Fuel level should decrease by the consumption rate after burnFuel() call")
    void testBurnFuel() {
        Plane plane = new Plane("TEST_PLANE", WaypointGenerator.prepareDescendingWaypoints(), DESCENDING);

        // Capture the plane's current consumption rate
        double consumption = Constant.CONSUMPTION_PER_SECOND;

        // Record initial fuel level
        double initialFuelLevel = plane.getFuelManager().getFuelLevel();

        // Trigger fuel burn
        plane.getFuelManager().burnFuel();

        // Verify the fuel level decreased by exactly the consumption rate
        double currentFuelLevel = plane.getFuelManager().getFuelLevel();

        assertEquals(currentFuelLevel, initialFuelLevel - consumption, "TEST_PLANE fuel level should be reduced");
    }
}

package unit_tests.airspace;

import com.jakub.bone.domain.airport.Airport;

import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.jakub.bone.domain.plane.Plane;

import java.sql.SQLException;

import static com.jakub.bone.domain.airport.Airport.runway1;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static com.jakub.bone.config.Constant.FINAL_APPROACH_CORRIDOR_1;

class RunwayTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneRepository mockPlaneRepository;
    @Mock
    CollisionRepository mockCollisionRepository;
    @InjectMocks
    ControlTowerService controlTower;
    Airport airport;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPlaneRepository()).thenReturn(mockPlaneRepository);
        when(mockDatabase.getCollisionRepository()).thenReturn(mockCollisionRepository);
        airport = new Airport();
    }

    @Test
    @DisplayName("Runway should initially be available")
    void testIsRunwayAvailableInitially(){
        // By default, the runway is assumed available
        assertTrue(controlTower.isRunwayAvailable(runway1),
                "Runway should be set as available at the beginning");
    }

    @Test
    @DisplayName("Runway can be set to unavailable")
    void testSetRunwayAsUnavailable(){
        runway1.setAvailable(false);
        assertFalse(controlTower.isRunwayAvailable(runway1),
                "Runway should be set as unavailable" );
    }

    @Test
    @DisplayName("Assigning a runway makes it unavailable")
    void testAssignRunwayMakesItOccupied(){
        // Once assigned, the runway is not available anymore
        controlTower.assignRunway(runway1);

        assertFalse(runway1.isAvailable(),
                "Runway should be unavailable (occupied) after assignment");
    }

    @Test
    @DisplayName("Releasing a runway makes it available again")
    void testReleaseRunway(){
        // Once released, the runway is available
        controlTower.releaseRunway(runway1);

        assertTrue(runway1.isAvailable(), "Runway should be available after being released");
    }

    @Test
    @DisplayName("Runway is released when a plane crosses final approach point")
    void testReleaseRunwayIfPlaneIsAtFinalAtApproach(){
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setLocation(FINAL_APPROACH_CORRIDOR_1);

        controlTower.releaseRunwayIfPlaneAtFinalApproach(plane, runway1);

        assertTrue(runway1.isAvailable(),
                "Runway should be released after the plane reaches the final approach point");
    }
}

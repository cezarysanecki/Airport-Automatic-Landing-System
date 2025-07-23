package unit_tests.flight;

import com.jakub.bone.domain.airport.Airport;

import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.service.FlightPhaseService;
import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.utils.WaypointGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utils.Messenger;

import java.io.IOException;
import java.sql.SQLException;

import static com.jakub.bone.domain.airport.Airport.runway1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.*;
import static com.jakub.bone.config.Constant.FINAL_APPROACH_CORRIDOR_1;

/*
 * This class tests how a plane's flight phase changes
 * based on its location and interactions with the ControlTower
 */
class FlightPhaseTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneRepository mockPlaneRepository;
    @Mock
    CollisionRepository mockCollisionRepository;
    @InjectMocks
    ControlTowerService controlTower;
    FlightPhaseService phaseCoordinator;
    Airport airport;
    Messenger messenger;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPlaneRepository()).thenReturn(mockPlaneRepository);
        when(mockDatabase.getCollisionRepository()).thenReturn(mockCollisionRepository);
        this.airport = new Airport();
        this.messenger = mock(Messenger.class);
        this.phaseCoordinator = new FlightPhaseService(controlTower, airport, messenger);
    }

    @Test
    @DisplayName("Should test flight phase set as DESCENDING after plane spawn")
    void testPhaseSettingToDescending() throws IOException, ClassNotFoundException {
        // Plane spawns at a certain altitude
        Plane plane = new Plane("TEST_PLANE");
        Location descentPoint = new Location(0, 0, 3000);

        phaseCoordinator.processFlightPhase(plane, descentPoint, null);

        assertEquals(DESCENDING, plane.getPhase(), "Flight phase should be set as DESCENDING");
    }

    @Test
    @DisplayName("Should test flight phase switch to holding")
    void testPhaseSwitchToHolding() throws IOException, ClassNotFoundException {
        // Plane has reached the end of its descent waypoints
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setCurrentIndex(WaypointGenerator.getDescentWaypoints().size());
        plane.descend();

        // Holding altitude = 1000
        Location holdingPoint = new Location(0, 0, 1000);
        phaseCoordinator.processFlightPhase(plane, holdingPoint, null);

        assertEquals(HOLDING, plane.getPhase(), "Flight phase should be switched to HOLDING");
    }

    @Test
    @DisplayName("Should test flight phase switch to LANDING when plane is at corridor entry point")
    void testPhaseSwitchToLandingAtCorridorEntryPoint() throws IOException, ClassNotFoundException {
        // Plane is currently in the holding pattern
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
        plane.setPhase(HOLDING);

        // Corridor entry triggers the switch to LANDING
        Location corridorEntry = runway1.getCorridor().getEntryPoint();
        phaseCoordinator.processFlightPhase(plane, corridorEntry, null);

        assertEquals(LANDING, plane.getPhase(), "Flight phase should be switched to LANDING");
    }

    @Test
    @DisplayName("Should test correct plane marking as landed after landing process")
    void testMarkingAsLanded(){
        // Plane is at the runway landing point
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setLocation(runway1.getLandingPoint());

        assertTrue(controlTower.hasLandedOnRunway(plane, runway1), "TEST_PLANE should be marked as landed");
    }

    @Test
    @DisplayName("Should test runway release after crossing final approach point")
    void testRunwayReleaseAfterCrossFinalApproach() {
        // Plane is exactly at final approach coordinates
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setLocation(FINAL_APPROACH_CORRIDOR_1);

        // Initially mark runway as unavailable
        runway1.setAvailable(false);

        // The plane crossing final approach triggers runway release
        controlTower.releaseRunwayIfPlaneAtFinalApproach(plane, runway1);

        assertTrue(runway1.isAvailable(), "Runway should be set as available");
    }
}

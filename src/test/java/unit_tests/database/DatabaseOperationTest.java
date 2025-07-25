package unit_tests.database;

import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.domain.airport.Airport;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.service.CollisionService;
import com.jakub.bone.service.ControlTowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static com.jakub.bone.config.Constant.Corridor.ENTRY_POINT_CORRIDOR_1;
import static com.jakub.bone.domain.airport.Airport.runway1;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseOperationTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneRepository mockPlaneRepository;
    @Mock
    CollisionRepository mockCollisionRepository;
    @InjectMocks
    ControlTowerService controlTower;
    CollisionService collisionDetector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPlaneRepository()).thenReturn(mockPlaneRepository);
        when(mockDatabase.getCollisionRepository()).thenReturn(mockCollisionRepository);
        collisionDetector = new CollisionService(controlTower);
    }

    @Test
    @DisplayName("Plane registration should be saved to the database")
    void testPlaneRegisterToDatabase() throws SQLException {
        Plane plane = new Plane("TEST_PLANE");

        // Registration should trigger a DB operation
        controlTower.registerPlane(plane);

        verify(mockPlaneRepository, times(1)).insertPlane(plane.getFlightNumber());
    }

    @Test
    @DisplayName("Landing should be saved to the database")
    void testLandingRegisterToDatabase() throws SQLException {
        Airport airport = new Airport();
        Plane plane = new Plane("TEST_PLANE");

        // Place the plane on the runway's landing point
        plane.getNavigator().setCoordinates(runway1.getLandingPoint());
        // Inform control tower that plane has landed
        controlTower.hasLandedOnRunway(plane, runway1);

        verify(mockPlaneRepository, times(1)).updateLandingTime(plane.getFlightNumber(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Collision should be saved to the database")
    void testCollisionRegisterToDatabase() throws SQLException {
        Plane plane1 = new Plane("TEST_PLANE_1");
        Plane plane2 = new Plane("TEST_PLANE_2");

        // Both planes share the same location => collision scenario
        plane1.getNavigator().setCoordinates(ENTRY_POINT_CORRIDOR_1);
        plane2.getNavigator().setCoordinates(ENTRY_POINT_CORRIDOR_1);

        controlTower.getPlanes().add(plane1);
        controlTower.getPlanes().add(plane2);

        collisionDetector.detectCollision();

        // Build the IDs array that the control tower will pass to the DAO
        String[] collidedIDs = {plane1.getFlightNumber(), plane2.getFlightNumber()};

        verify(mockCollisionRepository, times(1)).registerCollisionToDB(collidedIDs);
    }
}

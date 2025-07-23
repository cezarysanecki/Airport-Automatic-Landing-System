package unit_tests.airspace;

import com.jakub.bone.service.CollisionService;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import com.jakub.bone.domain.airport.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.jakub.bone.domain.plane.Plane;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CollisionTest {
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
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPlaneRepository()).thenReturn(mockPlaneRepository);
        when(mockDatabase.getCollisionRepository()).thenReturn(mockCollisionRepository);
        this.collisionDetector = new CollisionService(controlTower);
    }

    //Helper method to create a plane, set its location, and register
    Plane createAndRegisterPlane(String name, int x, int y, int altitude) {
        Plane plane = new Plane(name);
        plane.getNavigator().setLocation(new Location(x, y, altitude));
        controlTower.registerPlane(plane);
        return plane;
    }

    @Test
    @DisplayName("Should test collision if distance between planes is less than 10 meters")
    void tesPlanesCollisionAtTheSameLocalisation(){
        // Create two planes whose distance is under 10 meters
        Plane plane1 = createAndRegisterPlane("TEST_PLANE_1", 5000, 5000, 4010);
        Plane plane2 = createAndRegisterPlane("TEST_PLANE_2", 5000, 5000, 4000);

        collisionDetector.detectCollision();

        // Both planes should be destroyed
        assertTrue(plane1.isDestroyed(), "TEST_PLANE_1 should be destroyed after collision");
        assertTrue(plane2.isDestroyed(), "TEST_PLANE_2 should be destroyed after collision");
    }

    @Test
    @DisplayName("Should test collision registration if distance between planes equal 10 meters")
    void tesPlanesCollisionAtRiskZone(){
        // Create two planes whose distance is exactly 10 meters
        Plane plane1 = createAndRegisterPlane("TEST_PLANE_1", 5000, 5000, 4010);
        Plane plane2 = createAndRegisterPlane("TEST_PLANE_2", 5000, 5000, 4000);

        collisionDetector.detectCollision();

        // Both planes should be destroyed
        assertTrue(plane1.isDestroyed(), "TEST_PLANE_1 should be destroyed after collision");
        assertTrue(plane2.isDestroyed(), "TEST_PLANE_2 should be destroyed after collision");
    }

    @Test
    @DisplayName("Should test registration collision avoiding if planes beyond risk zone")
    void testPlanesCollisionBeyondRiskZone(){
        // Create two planes whose distance is more than 10 meters
        Plane plane1 = createAndRegisterPlane("TEST_PLANE_1", 5000, 5000, 4020);
        Plane plane2 = createAndRegisterPlane("TEST_PLANE_2", 5000, 5000, 4000);

        collisionDetector.detectCollision();

        // Both planes should remain intact
        assertFalse(plane1.isDestroyed(), "TEST_PLANE_1 should not be destroyed after collision");
        assertFalse(plane2.isDestroyed(), "TEST_PLANE_2 should not be destroyed after collision");
    }
}

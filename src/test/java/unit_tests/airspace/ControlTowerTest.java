package unit_tests.airspace;

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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ControlTowerTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneRepository mockPlaneRepository;
    @Mock
    CollisionRepository mockCollisionRepository;
    @InjectMocks
    ControlTowerService controlTower;
    List<Plane> incomingPlanes;
    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPlaneRepository()).thenReturn(mockPlaneRepository);
        when(mockDatabase.getCollisionRepository()).thenReturn(mockCollisionRepository);
        incomingPlanes = new ArrayList<>();
    }

    @Test
    @DisplayName("Should register all incoming planes correctly")
    void testRegisterPlane(){
        // Prepare a list of 10 planes
        for (int i = 0; i < 10; i++){
            incomingPlanes.add(new Plane("TEST_PLANE"));
        }

        // Register each plane
        for(Plane incoming: incomingPlanes){
            controlTower.registerPlane(incoming);
        }

        // Assert that all were registered
        assertEquals(10, controlTower.getPlanes().size(),
                "Plane list should contain exactly 10 planes");
        assertTrue(controlTower.getPlanes().size() == 10,
                "All incoming planes should be registered");
    }

    @Test
    @DisplayName("Should detect when airspace reaches maximum capacity")
        void testIsSpaceFull(){
        // Prepare a list of 110 planes
        for (int i = 0; i < 110; i++) {
            incomingPlanes.add(new Plane("TEST_PLANE"));
        }

        // Register each plane
        for (Plane plane : incomingPlanes) {
            controlTower.registerPlane(plane);
        }

        assertTrue(controlTower.isSpaceFull(),
                "Control tower should report that maximum capacity is reached");
    }

    @Test
    @DisplayName("Should detect when an incoming plane is within the collision risk zone")
    void testIfPlaneAtCollisionRiskZone() {
        // Register the first plane
        Plane plane1 = new Plane("TEST_PLANE_1");
        controlTower.registerPlane(plane1);

        // Retrieve its current index and shift the second plane's index by +1
        int referenceIndex = plane1.getNavigator().getCurrentIndex();

        Plane plane2 = new Plane("TEST_PLANE_2");
        plane2.getNavigator().setCurrentIndex(referenceIndex + 1);

        assertTrue(controlTower.isAtCollisionRiskZone(plane2),
                "Plane2 should be recognized as within collision risk zone relative to Plane1");
    }

    @Test
    @DisplayName("Should remove plane from airspace correctly")
    void testRemovePlaneFromSpace(){
        // Register plane
        Plane plane = new Plane("TEST_PLANE");
        controlTower.registerPlane(plane);

        // Remove plane
        controlTower.removePlaneFromSpace(plane);

        assertFalse(controlTower.getPlanes().contains(plane),
                "TEST_PLANE should be removed from airspace");
    }
}

package integration_tests;

import com.jakub.bone.infrastructure.PlaneClient;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.repository.CollisionRepository;
import com.jakub.bone.repository.PlaneRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.runners.AirportServer;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ClientServerConnectionTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneRepository mockPlaneRepository;
    @Mock
    CollisionRepository mockCollisionRepository;
    @InjectMocks
    ControlTowerService mockControlTower;
    AirportServer server;

    @BeforeEach
    void setUp() throws IOException, SQLException {

        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPlaneRepository()).thenReturn(mockPlaneRepository);
        when(mockDatabase.getCollisionRepository()).thenReturn(mockCollisionRepository);

            new Thread(() -> {
                try {
                    final AirportDatabase database = new AirportDatabase();
                    this.server = new AirportServer(database, new ControlTowerService(database));
                    this.server.setDatabase(mockDatabase);
                    this.server.setControlTowerService(mockControlTower);
                    this.server.startServer(5000);
                } catch (IOException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (server != null) {
            server.stopServer();
        }
    }

    /**
     * Helper method to wait for the server to start without repeating try-catch blocks
     */
    private void waitForUpdate() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("Should test single client connection")
    void testSingleClientConnection() {
        // Wait for the server to start
        waitForUpdate();

        PlaneClient planeClient = new PlaneClient("localhost", 5000);
        new Thread(planeClient).start();

        // Wait for the client to connect
        waitForUpdate();

        assertTrue(planeClient.isConnected(), "Client should successfully connect to the server");
    }

    @Test
    @DisplayName("Should test multiple clients connection")
    void testConnectionWithMultipleClients() {
        waitForUpdate();

        PlaneClient planeClient1 = new PlaneClient("localhost", 5000);
        new Thread(planeClient1).start();

        // Wait for the first client to connect
        waitForUpdate();

        PlaneClient planeClient2 = new PlaneClient("localhost", 5000);
        new Thread(planeClient2).start();

        // Wait for the second client to connect
        waitForUpdate();

        assertTrue(planeClient1.isConnected(), "Client1 should successfully connect to the server");
        assertTrue(planeClient2.isConnected(), "Client2 should successfully connect to the server");
    }

    @Test
    @DisplayName("Should test client registration")
    void testClientRegistration() {
        waitForUpdate();

        PlaneClient planeClient = new PlaneClient("localhost", 5000);
        new Thread(planeClient).start();

        waitForUpdate();

        assertEquals(1, server.getControlTowerService().getPlanes().size(),
                "Planes list should contain only one Plane object after registration");
    }

    @Test
    @DisplayName("Should test client registration with full capacity")
    void testClientRegistrationWithFullCapacity() {
        waitForUpdate();

        // Fill the list with 100 planes
        for(int i = 0; i < 100; i++){
            Plane plane = new Plane("TEST_PLANE");
            server.getControlTowerService().getPlanes().add(plane);
        }

        PlaneClient planeClient = new PlaneClient("localhost", 5000);
        new Thread(planeClient).start();

        waitForUpdate();

        assertEquals(100, server.getControlTowerService().getPlanes().size(),
                "Plane list should contain only 100 planes; the new plane should not be added");
    }
}

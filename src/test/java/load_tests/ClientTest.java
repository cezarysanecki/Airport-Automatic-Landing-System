package load_tests;

import com.jakub.bone.domain.plane.PlaneNumberFactory;
import com.jakub.bone.plane.client.PlaneClient;
import com.jakub.bone.config.Constant;
import com.jakub.bone.plane.message.Messenger;
import com.jakub.bone.utils.WaypointGenerator;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jakub.bone.domain.plane.FlightPhase.DESCENDING;

/*
 * Continuously spawns PlaneClient instances every 5 seconds,
 * simulating a sustained load for performance testing
 */
public class ClientTest {
    static final Logger logger = Logger.getLogger(ClientTest.class.getName());

    public static void main(String[] args) {
        // Automatically stop after 60 minutes
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Client stopped after 60 minutes");
                System.exit(0);
            }
        }, 3600000);

        ExecutorService executorService = Executors.newCachedThreadPool();

        while (true) {
            try {
                PlaneClient client = new PlaneClient("localhost", 5000, new Messenger(), new Plane(PlaneNumberFactory.generateFlightNumber().value(), WaypointGenerator.prepareDescendingWaypoints(), DESCENDING));
                executorService.execute(client);

                try {
                    Thread.sleep(Constant.CLIENT_SPAWN_DELAY);
                } catch (InterruptedException ex) {
                    logger.log(Level.WARNING, "Client thread interrupted", ex);
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "An error occurred", e);
            }
        }
    }
}


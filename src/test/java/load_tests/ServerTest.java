package load_tests;

import com.jakub.bone.runners.AirportServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class tests the behavior of the server under high load conditions
 */
public class ServerTest {
    static final Logger logger = Logger.getLogger(ClientTest.class.getName());
    public static void main(String[] args) throws IOException, SQLException {

        AirportServer airportServer = null;
        try {
            airportServer = new AirportServer();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Automatically stop after 70 minutes
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Server stopped after 70 minutes");
                System.exit(0);

            }
        }, 4200000);

        try {
            airportServer.startServer(5000);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to start the server:", ex.getMessage());
        }
    }
}
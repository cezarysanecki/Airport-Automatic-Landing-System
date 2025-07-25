package com.jakub.bone.runners;

import com.jakub.bone.api.control.PauseAirportServlet;
import com.jakub.bone.api.control.ResumeAirportServlet;
import com.jakub.bone.api.control.StartAirportServlet;
import com.jakub.bone.api.control.StopAirportServlet;
import com.jakub.bone.api.monitoring.CollisionsAirportServlet;
import com.jakub.bone.api.monitoring.PlanesAirportServlet;
import com.jakub.bone.api.monitoring.UptimeAirportServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.sql.Connection;
import java.sql.SQLException;

public class AirportServerContext extends ServletContextHandler {

    public final AirportServerFactory airportServerFactory;

    public AirportServerContext(Connection dbconnection) throws SQLException {
        addServlet(new ServletHolder(new StartAirportServlet()), "/airport/start");
        addServlet(new ServletHolder(new PauseAirportServlet()), "/airport/pause");
        addServlet(new ServletHolder(new ResumeAirportServlet()), "/airport/resume");
        addServlet(new ServletHolder(new StopAirportServlet()), "/airport/stop");
        addServlet(new ServletHolder(new UptimeAirportServlet()), "/airport/uptime");
        addServlet(new ServletHolder(new PlanesAirportServlet()), "/airport/planes/*");
        addServlet(new ServletHolder(new CollisionsAirportServlet()), "/airport/collisions");

        this.airportServerFactory = new AirportServerFactory(dbconnection);
    }
}

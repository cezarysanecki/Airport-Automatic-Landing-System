package com.jakub.bone.runners;

import com.jakub.bone.api.control.PauseAirportServlet;
import com.jakub.bone.api.control.ResumeAirportServlet;
import com.jakub.bone.api.control.StartAirportServlet;
import com.jakub.bone.api.control.StopAirportServlet;
import com.jakub.bone.api.monitoring.CollisionsAirportServlet;
import com.jakub.bone.api.monitoring.PlanesAirportServlet;
import com.jakub.bone.api.monitoring.UptimeAirportServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.sql.Connection;

public class ApiServerRunner {

    public static void run(Connection dbConnection, Server server) throws Exception {
        ServletContextHandler context = new ServletContextHandler();

        context.addServlet(new ServletHolder(new StartAirportServlet()), "/airport/start");
        context.addServlet(new ServletHolder(new PauseAirportServlet()), "/airport/pause");
        context.addServlet(new ServletHolder(new ResumeAirportServlet()), "/airport/resume");
        context.addServlet(new ServletHolder(new StopAirportServlet()), "/airport/stop");
        context.addServlet(new ServletHolder(new UptimeAirportServlet()), "/airport/uptime");
        context.addServlet(new ServletHolder(new PlanesAirportServlet()), "/airport/planes/*");
        context.addServlet(new ServletHolder(new CollisionsAirportServlet()), "/airport/collisions");

        context.setAttribute("airportServerFactory", new AirportServerFactory(dbConnection));

        server.setHandler(context);

        server.start();
        System.out.println("Server is running on http://localhost:8080");
    }
}

package com.jakub.bone.utils;

import com.google.gson.Gson;
import com.jakub.bone.plane.server.PlaneHandlerServer.AirportInstruction;
import com.jakub.bone.shared.Coordinates;
import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.plane.Plane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Messenger {

    private static final Gson GSON = new Gson();

    public static void send(ObjectOutputStream out, Double message) throws IOException {
        sendGeneric(out, message);
    }

    public static void send(ObjectOutputStream out, Runway message) throws IOException {
        sendGeneric(out, message);
    }

    public static void send(ObjectOutputStream out, Coordinates message) throws IOException {
        sendGeneric(out, message);
    }

    public static void send(ObjectOutputStream out, Plane message) throws IOException {
        sendGeneric(out, message);
    }

    public static void send(ObjectOutputStream out, AirportInstruction message) throws IOException {
        sendGeneric(out, message);
    }

    public static AirportInstruction handleResponseAirportInstruction(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return handleResponse(in, AirportInstruction.class);
    }

    public static Runway handleResponseRunway(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return handleResponse(in, Runway.class);
    }

    public static Coordinates handleResponseCoordinates(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return handleResponse(in, Coordinates.class);
    }

    public static Double handleResponseFuelLevel(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return handleResponse(in, Double.class);
    }

    public static Plane handleResponsePlane(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return handleResponse(in, Plane.class);
    }

    private static void sendGeneric(ObjectOutputStream out, Object message) throws IOException {
        if (message instanceof Integer) {
            // Send the enum as a plain string
            out.writeObject(((Integer) message).toString());
        } else {
            // Serialize other objects as JSON
            String jsonMessage = GSON.toJson(message);
            out.writeObject(jsonMessage);
        }
        out.flush();
    }

    private static <T> T handleResponse(ObjectInputStream in, Class<T> type) throws IOException, ClassNotFoundException {
        String json = (String) in.readObject();
        return GSON.fromJson(json, type);
    }

}

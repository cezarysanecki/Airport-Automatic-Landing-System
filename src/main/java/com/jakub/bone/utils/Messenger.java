package com.jakub.bone.utils;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Messenger {
    private final Gson gson;

    public Messenger() {
        this.gson = new Gson();
    }

    // Sending with ObjectOutputStream
    public void send(Object message, ObjectOutputStream out) throws IOException {
        if (message instanceof Integer) {
            // Send the enum as a plain string
            out.writeObject(((Integer) message).toString());
        } else {
            // Serialize other objects as JSON
            String jsonMessage = gson.toJson(message);
            out.writeObject(jsonMessage);
        }
        out.flush();

    }

    // Sending with HttpServletResponse (REST API)
    public void send(HttpServletResponse response, Object message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonMessage = gson.toJson(message);
        response.getWriter().write(jsonMessage);
    }

    public <T> T receiveAndParse(ObjectInputStream in, Class<T> type) throws IOException, ClassNotFoundException {
        String json = (String) in.readObject();
        return gson.fromJson(json, type);
    }
}

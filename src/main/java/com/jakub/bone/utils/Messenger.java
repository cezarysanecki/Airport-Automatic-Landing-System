package com.jakub.bone.utils;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Messenger {

    private final Gson gson;

    public Messenger() {
        this.gson = new Gson();
    }

    // Sending with ObjectOutputStream
    public void send(ObjectOutputStream out, Object message) throws IOException {
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

    public <T> T receiveAndParse(ObjectInputStream in, Class<T> type) throws IOException, ClassNotFoundException {
        String json = (String) in.readObject();
        return gson.fromJson(json, type);
    }

}

package com.jakub.bone.plane.message;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class Messenger {

    private static final Gson GSON = new Gson();

    static void sendGeneric(ObjectOutputStream out, Object message) throws IOException {
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

    static <T> T handleResponse(ObjectInputStream in, Class<T> type) throws IOException, ClassNotFoundException {
        String json = (String) in.readObject();
        return GSON.fromJson(json, type);
    }

}

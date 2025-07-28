package com.jakub.bone.api;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class JsonSender {

    private static final Gson GSON = new Gson();

    public static void responseWithJson(HttpServletResponse response, Map<String, Object> message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonMessage = GSON.toJson(message);
        response.getWriter().write(jsonMessage);
    }

}

package org.example;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

@ServerEndpoint("/leaderboard")
public class LeaderboardWebSocket {
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Received null or empty message");
            return;
        }

        try {
            JsonElement jsonElement = JsonParser.parseString(message);
            if (!jsonElement.isJsonObject()) {
                System.out.println("Received invalid JSON message");
                return;
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Received malformed JSON message");
            return;
        }

        System.out.println("Received: " + message);
        broadcastUpdate("Leaderboard updated: " + message);
    }

    public static void broadcastUpdate(String updateMessage) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(updateMessage);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Error broadcasting message: " + e.getMessage());
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }
}

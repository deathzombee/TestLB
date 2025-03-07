package org.example;

import jakarta.websocket.server.ServerEndpointConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;

public class App {
    public static void main(String[] args) {
        int port = 8080; // WebSocket server port

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Initialize Jakarta WebSockets with Jetty
        JakartaWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
            wsContainer.addEndpoint(ServerEndpointConfig.Builder.create(LeaderboardWebSocket.class, "/leaderboard").build());
        });

        try {
            server.start();
            System.out.println("WebSocket server started on ws://localhost:" + port + "/leaderboard");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


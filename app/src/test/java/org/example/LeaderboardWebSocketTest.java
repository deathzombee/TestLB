package org.example;

import org.junit.jupiter.api.*;
import jakarta.websocket.*;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeaderboardWebSocketTest {

    private static final String SERVER_URI = "ws://localhost:8080/leaderboard";
    private static CountDownLatch messageLatch;
    private static String receivedMessage;
    
    @ClientEndpoint
    public static class TestClient {
        @OnMessage
        public void onMessage(String message) {
            receivedMessage = message;
            messageLatch.countDown();
        }
    }

    @BeforeAll
    public static void setup() throws Exception {
        // Start server (if not already running)
        new Thread(() -> App.main(new String[]{})).start();
        Thread.sleep(2000); // Give server time to start
    }

    @Test
    public void testWebSocketServer() throws Exception {
        messageLatch = new CountDownLatch(1);

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(TestClient.class, new URI(SERVER_URI));

        // Send a test message
        session.getBasicRemote().sendText("{\"player\": \"DZ\", \"score\": 1500}");

        // Wait for a response
        boolean messageReceived = messageLatch.await(3, TimeUnit.SECONDS);
        
        // Check if message was received
        assertTrue(messageReceived, "Server should respond with a leaderboard update");
        assertNotNull(receivedMessage, "Received message should not be null");

        session.close();
    }
}


package com.github.blckrbbit.server;

import com.github.blckrbbit.TCPConnection;
import com.github.blckrbbit.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.blckrbbit.PropertyLoader.load;
import static java.util.logging.Logger.getLogger;

public class ChatServer implements TCPConnectionListener {
    private final List<TCPConnection> connections = new ArrayList<>();
    private final String PATH_TO_PROPERTIES = "server/src/resources/application.properties";
    private final Logger logger = getLogger(ChatServer.class.getName());

    private ChatServer() {
//        System.out.println("Server running...");
        logger.log(Level.INFO, "Server running...");
        try(ServerSocket socket = new ServerSocket(
                Integer.parseInt(load(PATH_TO_PROPERTIES)
                        .properties()
                        .getProperty("port")))
        ) {
            while (true) {
                try {
                    new TCPConnection(this, socket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                    logger.log(Level.WARNING,  "TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void run() {
        new ChatServer();
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendMessageToAllClients("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveMessage(TCPConnection tcpConnection, String message) {
        sendMessageToAllClients(message);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendMessageToAllClients("Client disconnected: " + tcpConnection);
        logger.log(Level.INFO, "Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
        logger.log(Level.INFO, "TCPConnection exception: " + e);
    }

    private void sendMessageToAllClients(String message) {
        for(TCPConnection connection : connections) connection.sendMessage(message);
    }


}

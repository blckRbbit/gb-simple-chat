package com.github.blckrbbit;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {
    private final Socket socket;
    private final Thread inputConnectionThread;
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String host, int port) throws IOException{
        this(eventListener, new Socket(host, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        inputConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!inputConnectionThread.isInterrupted()) {
                        eventListener.onReceiveMessage(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        inputConnectionThread.start();
    }

    public synchronized void sendMessage(String message) {
        try {
            out.write(message +"\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        inputConnectionThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}

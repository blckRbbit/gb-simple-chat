package com.github.blckrbbit.client;

import com.github.blckrbbit.TCPConnection;
import com.github.blckrbbit.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private TCPConnection connection;
    private final JTextArea messagesLog = new JTextArea();
    private final JTextField loginField = new JTextField("alex");
    private final JTextField messageField = new JTextField();

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        messagesLog.setEditable(false);
        messagesLog.setLineWrap(true);
        add(messagesLog, BorderLayout.CENTER);

        messageField.addActionListener(this);
        add(loginField, BorderLayout.NORTH);
        add(messageField, BorderLayout.SOUTH);


        setVisible(true);
        try {
            connection = new TCPConnection(this, HOST, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    public static void run() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = messageField.getText();
        if (message.equals("")) return;
        messageField.setText(null);
        connection.sendMessage(loginField.getText() + ": " + message);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready.");
    }

    @Override
    public void onReceiveMessage(TCPConnection tcpConnection, String message) {
        printMessage(message);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close.");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                messagesLog.append(message + "\n");
                messagesLog.setCaretPosition(messagesLog.getDocument().getLength());
            }
        });
    }
}

package com.github.blckrbbit;

public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection tcpConnection);
    void onReceiveMessage(TCPConnection tcpConnection, String message);
    void onDisconnect(TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection, Exception e);
}

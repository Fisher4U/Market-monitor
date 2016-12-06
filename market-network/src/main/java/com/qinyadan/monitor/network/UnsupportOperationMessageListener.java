package com.qinyadan.monitor.network;

import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.packet.SendPacket;

public class UnsupportOperationMessageListener implements MessageListener {

    private static final UnsupportOperationMessageListener INSTANCE = new UnsupportOperationMessageListener();

    @Override
    public void handleSend(SendPacket sendPacket, DuplexSocket duplexSocket) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Unsupported handleSend method");
        errorMessage.append("packet:").append(sendPacket);
        errorMessage.append(", remote::").append(duplexSocket.getRemoteAddress());

        throw new UnsupportedOperationException(errorMessage.toString());
    }

    @Override
    public void handleRequest(RequestPacket requestPacket, DuplexSocket duplexSocket) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Unsupported handleRequest method");
        errorMessage.append("packet:").append(requestPacket);
        errorMessage.append(", remote::").append(duplexSocket.getRemoteAddress());
        throw new UnsupportedOperationException(errorMessage.toString());
    }

    public static UnsupportOperationMessageListener getInstance() {
        return INSTANCE;
    }

}


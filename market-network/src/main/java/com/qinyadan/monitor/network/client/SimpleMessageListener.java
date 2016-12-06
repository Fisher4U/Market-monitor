package com.qinyadan.monitor.network.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.MessageListener;
import com.qinyadan.monitor.network.DuplexSocket;
import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.packet.SendPacket;

public class SimpleMessageListener implements MessageListener {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final SimpleMessageListener INSTANCE = new SimpleMessageListener();
    public static final SimpleMessageListener ECHO_INSTANCE = new SimpleMessageListener(true);

    private final boolean echo;

    public SimpleMessageListener() {
        this(false);
    }

    public SimpleMessageListener(boolean echo) {
        this.echo = echo;
    }

    @Override
    public void handleSend(SendPacket sendPacket, DuplexSocket duplexSocket) {
        logger.info("handleSend packet:{}, remote:{},message:{}", sendPacket, duplexSocket.getRemoteAddress(),new String(sendPacket.getPayload()));
    }

    @Override
    public void handleRequest(RequestPacket requestPacket, DuplexSocket duplexSocket) {
        logger.info("handleRequest packet:{}, remote:{}", requestPacket, duplexSocket.getRemoteAddress());

        if (echo) {
            duplexSocket.response(requestPacket, requestPacket.getPayload());
        } else {
            duplexSocket.response(requestPacket, new byte[0]);
        }
    }

}

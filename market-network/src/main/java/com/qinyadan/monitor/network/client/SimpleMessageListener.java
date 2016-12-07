package com.qinyadan.monitor.network.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.DuplexSocket;
import com.qinyadan.monitor.network.MessageListener;
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
        logger.info("handleSend packet:{}, remote:{},message:{}", sendPacket, duplexSocket.getRemoteAddress(),getSerializedBytes(sendPacket.getPayload()));
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
    
    private static Map getSerializedBytes(byte[] bytes)  {
		if (null == bytes )
			return null;
		try {
			ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
		    ObjectInputStream in = new ObjectInputStream(byteIn);
		    Map<Integer, String> data2 = (Map<Integer, String>) in.readObject();
		    return data2;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}

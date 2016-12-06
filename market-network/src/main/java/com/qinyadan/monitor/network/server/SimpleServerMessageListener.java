package com.qinyadan.monitor.network.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.client.SimpleMessageListener;
import com.qinyadan.monitor.network.packet.HandshakeResponseCode;
import com.qinyadan.monitor.network.packet.HandshakeResponseType;
import com.qinyadan.monitor.network.packet.PingPacket;

public class SimpleServerMessageListener extends SimpleMessageListener implements ServerMessageListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final SimpleServerMessageListener SIMPLEX_INSTANCE = new SimpleServerMessageListener(
			HandshakeResponseType.Success.SIMPLEX_COMMUNICATION);
	public static final SimpleServerMessageListener DUPLEX_INSTANCE = new SimpleServerMessageListener(
			HandshakeResponseType.Success.DUPLEX_COMMUNICATION);

	public static final SimpleServerMessageListener SIMPLEX_ECHO_INSTANCE = new SimpleServerMessageListener(true,
			HandshakeResponseType.Success.SIMPLEX_COMMUNICATION);
	public static final SimpleServerMessageListener DUPLEX_ECHO_INSTANCE = new SimpleServerMessageListener(true,
			HandshakeResponseType.Success.DUPLEX_COMMUNICATION);

	private final HandshakeResponseCode handshakeResponseCode;

	public SimpleServerMessageListener(HandshakeResponseCode handshakeResponseCode) {
		this(false, handshakeResponseCode);
	}

	public SimpleServerMessageListener(boolean echo, HandshakeResponseCode handshakeResponseCode) {
		super(echo);
		this.handshakeResponseCode = handshakeResponseCode;
	}

	@Override
	public void handlePing(PingPacket pingPacket, MonitorServer monitorServer) {
		logger.info("handlePing packet:{}, remote:{} ", pingPacket, monitorServer.getRemoteAddress());
	}

}

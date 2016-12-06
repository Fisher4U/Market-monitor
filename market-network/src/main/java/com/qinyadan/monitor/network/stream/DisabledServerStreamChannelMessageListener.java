package com.qinyadan.monitor.network.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.packet.stream.StreamClosePacket;
import com.qinyadan.monitor.network.packet.stream.StreamCode;
import com.qinyadan.monitor.network.packet.stream.StreamCreatePacket;

public class DisabledServerStreamChannelMessageListener implements ServerStreamChannelMessageListener {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final ServerStreamChannelMessageListener INSTANCE = new DisabledServerStreamChannelMessageListener();

	@Override
	public StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet) {
		logger.info("{} handleStreamCreate unsupported operation. StreamChannel:{}, Packet:{}",
				this.getClass().getSimpleName(), streamChannelContext, packet);
		return StreamCode.CONNECTION_UNSUPPORT;
	}

	@Override
	public void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet) {
		logger.info("{} handleStreamClose unsupported operation. StreamChannel:{}, Packet:{}",
				this.getClass().getSimpleName(), streamChannelContext, packet);
	}

}

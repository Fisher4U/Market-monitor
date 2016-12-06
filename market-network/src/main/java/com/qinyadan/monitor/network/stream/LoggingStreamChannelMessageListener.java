package com.qinyadan.monitor.network.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.packet.stream.StreamClosePacket;
import com.qinyadan.monitor.network.packet.stream.StreamCode;
import com.qinyadan.monitor.network.packet.stream.StreamCreatePacket;
import com.qinyadan.monitor.network.packet.stream.StreamResponsePacket;

public class LoggingStreamChannelMessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingStreamChannelMessageListener.class);

	public static final ServerStreamChannelMessageListener SERVER_LISTENER = new Server();
	public static final ClientStreamChannelMessageListener CLIENT_LISTENER = new Client();

	static class Server implements ServerStreamChannelMessageListener {

		@Override
		public StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext,
				StreamCreatePacket packet) {
			LOGGER.info("handleStreamCreate StreamChannel:{}, Packet:{}", streamChannelContext, packet);
			return StreamCode.OK;
		}

		@Override
		public void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet) {
			LOGGER.info("handleStreamClose StreamChannel:{}, Packet:{}", streamChannelContext, packet);
		}

	}

	static class Client implements ClientStreamChannelMessageListener {

		@Override
		public void handleStreamData(ClientStreamChannelContext streamChannelContext, StreamResponsePacket packet) {
			LOGGER.debug("handleStreamData StreamChannel:{}, Packet:{}", streamChannelContext, packet);
		}

		@Override
		public void handleStreamClose(ClientStreamChannelContext streamChannelContext, StreamClosePacket packet) {
			LOGGER.info("handleStreamClose StreamChannel:{}, Packet:{}", streamChannelContext, packet);
		}

	}
}

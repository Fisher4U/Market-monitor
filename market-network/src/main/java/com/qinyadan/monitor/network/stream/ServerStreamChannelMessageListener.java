package com.qinyadan.monitor.network.stream;

import com.qinyadan.monitor.network.packet.stream.StreamClosePacket;
import com.qinyadan.monitor.network.packet.stream.StreamCode;
import com.qinyadan.monitor.network.packet.stream.StreamCreatePacket;

public interface ServerStreamChannelMessageListener {

	StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet);
	void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet);

}

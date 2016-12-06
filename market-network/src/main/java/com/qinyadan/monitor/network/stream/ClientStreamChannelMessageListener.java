package com.qinyadan.monitor.network.stream;

import com.qinyadan.monitor.network.packet.stream.StreamClosePacket;
import com.qinyadan.monitor.network.packet.stream.StreamResponsePacket;

public interface ClientStreamChannelMessageListener {

	void handleStreamData(ClientStreamChannelContext streamChannelContext, StreamResponsePacket packet);

	void handleStreamClose(ClientStreamChannelContext streamChannelContext, StreamClosePacket packet);

}

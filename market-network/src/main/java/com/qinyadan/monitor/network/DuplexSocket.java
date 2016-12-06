package com.qinyadan.monitor.network;

import java.net.SocketAddress;

import com.qinyadan.monitor.network.cluster.ClusterOption;
import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.stream.ClientStreamChannel;
import com.qinyadan.monitor.network.stream.ClientStreamChannelContext;
import com.qinyadan.monitor.network.stream.ClientStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.StreamChannelStateChangeEventHandler;

public interface DuplexSocket {

	void send(byte[] payload);

	Future<ResponseMessage> request(byte[] payload);

	void response(RequestPacket requestPacket, byte[] payload);

	void response(int requestId, byte[] payload);

	ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener);

	ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener,
			StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener);

	SocketAddress getRemoteAddress();

	void close();

	ClusterOption getLocalClusterOption();

	ClusterOption getRemoteClusterOption();

}

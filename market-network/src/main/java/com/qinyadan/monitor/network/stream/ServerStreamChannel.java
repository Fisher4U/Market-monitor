package com.qinyadan.monitor.network.stream;

import com.qinyadan.monitor.network.packet.stream.StreamCreateSuccessPacket;
import com.qinyadan.monitor.network.packet.stream.StreamResponsePacket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class ServerStreamChannel extends StreamChannel {

	public ServerStreamChannel(Channel channel, int streamId, StreamChannelManager streamChannelManager) {
		super(channel, streamId, streamChannelManager);
	}

	public ChannelFuture sendData(byte[] payload) {
		assertState(StreamChannelStateCode.CONNECTED);

		StreamResponsePacket dataPacket = new StreamResponsePacket(getStreamId(), payload);
		return this.getChannel().write(dataPacket);
	}

	public ChannelFuture sendCreateSuccess() {
		assertState(StreamChannelStateCode.CONNECTED);

		StreamCreateSuccessPacket packet = new StreamCreateSuccessPacket(getStreamId());
		return this.getChannel().write(packet);
	}

	boolean changeStateConnectArrived() {
		return changeStateTo(StreamChannelStateCode.CONNECT_ARRIVED);
	}

}

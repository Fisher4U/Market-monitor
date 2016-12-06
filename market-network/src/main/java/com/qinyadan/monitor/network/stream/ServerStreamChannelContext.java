package com.qinyadan.monitor.network.stream;

public class ServerStreamChannelContext extends StreamChannelContext {

	private ServerStreamChannel streamChannel;

	public ServerStreamChannelContext(ServerStreamChannel streamChannel) {
		this.streamChannel = streamChannel;
	}

	@Override
	public ServerStreamChannel getStreamChannel() {
		return streamChannel;
	}

}

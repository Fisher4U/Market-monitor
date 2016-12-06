package com.qinyadan.monitor.network.server;

import com.qinyadan.monitor.network.codec.PacketDecoder;
import com.qinyadan.monitor.network.codec.PacketEncoder;
import com.qinyadan.monitor.network.server.handler.ServerChannelHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

@SuppressWarnings("rawtypes")
public class ServerChannelInitializer extends ChannelInitializer {
	
	private final DefaultMonitorServer pinpointServer;
	
	public ServerChannelInitializer(DefaultMonitorServer pinpointServer) {
        if (pinpointServer == null) {
            throw new NullPointerException("pinpointServer");
        }
        this.pinpointServer = pinpointServer;
    }

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("decoder", new PacketDecoder());
		pipeline.addLast("idleState", new IdleStateHandler(20, 10, 0));
        pipeline.addLast("handler", new ServerChannelHandler(pinpointServer));
	}

}

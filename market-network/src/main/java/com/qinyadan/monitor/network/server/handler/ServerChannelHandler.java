package com.qinyadan.monitor.network.server.handler;

import com.qinyadan.monitor.network.server.DefaultMonitorServer;
import com.qinyadan.monitor.network.util.LoggerFactorySetup;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

	static {
		LoggerFactorySetup.setupSlf4jLoggerFactory();
	}

	private final DefaultMonitorServer monitorServer;

	public ServerChannelHandler(DefaultMonitorServer monitorServer) {
		this.monitorServer = monitorServer;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
		monitorServer.setChannel(ctx.channel());
		if (monitorServer != null) {
			monitorServer.messageReceived(message);
		}
	}

}

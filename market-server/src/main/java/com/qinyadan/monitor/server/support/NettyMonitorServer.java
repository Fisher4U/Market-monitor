package com.qinyadan.monitor.server.support;

import com.qinyadan.monitor.server.MonitorServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyMonitorServer implements MonitorServer {

	private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();

	@Override
	public void start() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
						@Override
						public void initChannel(io.netty.channel.socket.SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
							p.addLast("frameEncoder", new LengthFieldPrepender(4));
							p.addLast("decoder", new ByteArrayDecoder());
							p.addLast("encoder", new ByteArrayEncoder());
							p.addLast(new CollectionServerDataHandler());
						}
					});

			ChannelFuture f = b.bind(8889).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {

		}
	}

	@Override
	public void shutdown() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

}

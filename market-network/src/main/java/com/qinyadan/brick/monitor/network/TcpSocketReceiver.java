package com.qinyadan.brick.monitor.network;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.config.ServerTransportConfiguration;
import com.qinyadan.brick.monitor.network.codec.DecodeHandler;
import com.qinyadan.brick.monitor.network.codec.DecodeHandlerManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

public final class TcpSocketReceiver {

	private static final Logger logger = LoggerFactory.getLogger(TcpSocketReceiver.class);

	private DecodeHandlerManager manager;
	private ChannelFuture future;
	private int port;

	private Class<? extends ServerSocketChannel> channelClass;
	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;

	public TcpSocketReceiver(ServerTransportConfiguration config) {
		
		int bossThreads = config.getBossThreads();
		int workerThreads = config.getWorkerThreads();
		boolean linux = getOSMatches("Linux") || getOSMatches("LINUX");
		port = config.getTcpPort();
		bossGroup = linux ? new EpollEventLoopGroup(bossThreads) : new NioEventLoopGroup(bossThreads);
		workerGroup = linux ? new EpollEventLoopGroup(workerThreads) : new NioEventLoopGroup(workerThreads);
		channelClass = linux ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
	}

	public synchronized void init() throws Exception {
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(bossGroup, workerGroup).channel(channelClass);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("decode", new MessageDecoder());
			}
		});

		bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		try {
			future = bootstrap.bind(port).sync();
			logger.info(String.format("Market monitor is listening on port %s", port));
		} catch (Exception e) {
			logger.error(String.format("Error when binding to port %s!", port), e);
			throw e;
		}
	}

	public synchronized void destory() {
		try {
			future.channel().closeFuture();
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			logger.info(String.format("Netty server stopped on port %s", port));
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}

	private boolean getOSMatches(String osNamePrefix) {
		String os = System.getProperty("os.name");

		if (os == null) {
			return false;
		}

		return os.startsWith(osNamePrefix);
	}

	public class MessageDecoder extends ByteToMessageDecoder {
		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
			if (buffer.readableBytes() < 4) {
				return;
			}

			buffer.markReaderIndex();

			int length = buffer.readInt();

			buffer.resetReaderIndex();

			if (buffer.readableBytes() < length + 4) {
				return;
			}

			buffer.readInt(); // get rid of length

			ByteBuf buf = buffer.readSlice(length);
			DecodeHandler handler = manager.getHandler(buf);

			if (handler != null) {
				buf.retain(); // hold reference to avoid being GC, the buf will
								// be released in dump analyzer
				handler.handle(buf);
			}
		}
	}
}
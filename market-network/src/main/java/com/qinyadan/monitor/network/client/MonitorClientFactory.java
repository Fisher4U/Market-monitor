package com.qinyadan.monitor.network.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.MessageListener;
import com.qinyadan.monitor.network.MonitorSocketException;
import com.qinyadan.monitor.network.cluster.ClusterOption;
import com.qinyadan.monitor.network.cluster.Role;
import com.qinyadan.monitor.network.codec.PacketDecoder;
import com.qinyadan.monitor.network.codec.PacketEncoder;
import com.qinyadan.monitor.network.stream.DisabledServerStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.ServerStreamChannelMessageListener;
import com.qinyadan.monitor.network.util.AssertUtils;
import com.qinyadan.monitor.network.util.LoggerFactorySetup;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class MonitorClientFactory {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	static {
		LoggerFactorySetup.setupSlf4jLoggerFactory();
	}

	public static final String CONNECT_TIMEOUT_MILLIS = "connectTimeoutMillis";
	private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
	private static final long DEFAULT_TIMEOUTMILLIS = 3 * 1000;
	private static final long DEFAULT_PING_DELAY = 60 * 1000 * 5;
	private static final long DEFAULT_ENABLE_WORKER_PACKET_DELAY = 60 * 1000 * 1;

	private final AtomicInteger socketId = new AtomicInteger(1);
	private volatile boolean released;
	private Map<String, Object> properties = Collections.emptyMap();
	private long reconnectDelay = 3 * 1000;
	private long pingDelay = DEFAULT_PING_DELAY;
	private long enableWorkerPacketDelay = DEFAULT_ENABLE_WORKER_PACKET_DELAY;
	private long timeoutMillis = DEFAULT_TIMEOUTMILLIS;
	private ClusterOption clusterOption = ClusterOption.DISABLE_CLUSTER_OPTION;

	private MessageListener messageListener = SimpleMessageListener.INSTANCE;
	private ServerStreamChannelMessageListener serverStreamChannelMessageListener = DisabledServerStreamChannelMessageListener.INSTANCE;
	private final EventLoopGroup eventLoopGroupWorker;
	private final Bootstrap bootstrap = new Bootstrap();

	public MonitorClientFactory() {
		this(1);
	}

	public MonitorClientFactory(int workerCount) {
		eventLoopGroupWorker = new NioEventLoopGroup(workerCount, new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("NettyClientSelector_%d", this.threadIndex.incrementAndGet()));
			}
		});
	}

	public MonitorClient connect(String host, int port) throws MonitorSocketException {
		InetSocketAddress connectAddress = new InetSocketAddress(host, port);
		return connect(connectAddress);
	}

	public MonitorClient connect(InetSocketAddress connectAddress) throws MonitorSocketException {
		ChannelFuture connectFuture;
		try {
			this.bootstrap.group(eventLoopGroupWorker).channel(NioSocketChannel.class)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.SO_SNDBUF, 1024 * 64)
					.option(ChannelOption.SO_RCVBUF, 1024 * 64)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("encoder", new PacketEncoder());
							pipeline.addLast("decoder", new PacketDecoder());
							pipeline.addLast("idleState", new IdleStateHandler(20, 10, 0));
							DefaultClientHandler defaultClientHandler = new DefaultClientHandler(MonitorClientFactory.this);
							pipeline.addLast("socketHandler", defaultClientHandler);
						}
					});
			connectFuture = bootstrap.connect(connectAddress).sync();

			DuplexClient pinpointClientHandler = getSocketHandler(connectFuture, connectAddress);
			MonitorClient monitorClient = new MonitorClient(pinpointClientHandler);
			return monitorClient;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}

	}

	public MonitorClient reconnect(String host, int port) throws MonitorSocketException {
		SocketAddress address = new InetSocketAddress(host, port);
		ChannelFuture connectFuture = bootstrap.connect(address);
		DuplexClient pinpointClientHandler = getSocketHandler(connectFuture, address);
		MonitorClient monitorClient = new MonitorClient(pinpointClientHandler);
		return monitorClient;
	}

	DuplexClient getSocketHandler(ChannelFuture channelConnectFuture, SocketAddress address) {
		if (address == null) {
			throw new NullPointerException("address");
		}
		DuplexClient pinpointClientHandler = getSocketHandler(channelConnectFuture.channel());
		pinpointClientHandler.setConnectSocketAddress(address);

		ConnectFuture handlerConnectFuture = pinpointClientHandler.getConnectFuture();
		handlerConnectFuture.awaitUninterruptibly();

		if (ConnectFuture.Result.FAIL == handlerConnectFuture.getResult()) {
			throw new MonitorSocketException("connect fail to " + address + ".", channelConnectFuture.cause());
		}
		return pinpointClientHandler;
	}

	public ChannelFuture reconnect(final SocketAddress remoteAddress) {
		if (remoteAddress == null) {
			throw new NullPointerException("remoteAddress");
		}

		ChannelPipeline pipeline;
		final Bootstrap bootstrap = this.bootstrap;
		Channel ch = bootstrap.bind(remoteAddress).channel();
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT)
				.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_SNDBUF, 1024 * 64).option(ChannelOption.SO_RCVBUF, 1024 * 64);
		try {
			pipeline = ch.pipeline();
		} catch (Exception e) {
			throw new ChannelPipelineException("Failed to initialize a pipeline.", e);
		}
		DuplexClient pinpointClientHandler = (DefaultClientHandler) pipeline.get("socketHandler");
		pinpointClientHandler.initReconnect();

		// Set the options.
		boolean success = false;
		try {
			success = true;
		} finally {
			if (!success) {
				ch.close();
			}
		}
		// Connect.
		return ch.connect(remoteAddress);
	}

	private DuplexClient getSocketHandler(Channel channel) {
		DefaultClientHandler handle = (DefaultClientHandler) channel.pipeline().get("socketHandler");
		return handle;
	}

	public void release() {
		synchronized (this) {
			if (released) {
				return;
			}
			released = true;
		}

		if (bootstrap != null) {
			bootstrap.group().shutdownGracefully();
		}
	}

	Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> agentProperties) {
		AssertUtils.assertNotNull(properties, "agentProperties must not be null");

		this.properties = Collections.unmodifiableMap(agentProperties);
	}

	public ClusterOption getClusterOption() {
		return clusterOption;
	}

	public void setClusterOption(String id, List<Role> roles) {
		this.clusterOption = new ClusterOption(true, id, roles);
	}

	public void setClusterOption(ClusterOption clusterOption) {
		this.clusterOption = clusterOption;
	}

	public MessageListener getMessageListener() {
		return messageListener;
	}

	public MessageListener getMessageListener(MessageListener defaultMessageListener) {
		if (messageListener == null) {
			return defaultMessageListener;
		}

		return messageListener;
	}

	public void setMessageListener(MessageListener messageListener) {
		AssertUtils.assertNotNull(messageListener, "messageListener must not be null");

		this.messageListener = messageListener;
	}

	public ServerStreamChannelMessageListener getServerStreamChannelMessageListener() {
		return serverStreamChannelMessageListener;
	}

	public ServerStreamChannelMessageListener getServerStreamChannelMessageListener(
			ServerStreamChannelMessageListener defaultStreamMessageListener) {
		if (serverStreamChannelMessageListener == null) {
			return defaultStreamMessageListener;
		}

		return serverStreamChannelMessageListener;
	}

	public void setServerStreamChannelMessageListener(
			ServerStreamChannelMessageListener serverStreamChannelMessageListener) {
		AssertUtils.assertNotNull(messageListener, "messageListener must not be null");

		this.serverStreamChannelMessageListener = serverStreamChannelMessageListener;
	}

	boolean isReleased() {
		return released;
	}

	public int issueNewSocketId() {
		return socketId.getAndIncrement();
	}

	public void setConnectTimeout(int connectTimeout) {
		if (connectTimeout < 0) {
			throw new IllegalArgumentException("connectTimeout cannot be a negative number");
		}
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
	}

	public long getReconnectDelay() {
		return reconnectDelay;
	}

	public void setReconnectDelay(long reconnectDelay) {
		if (reconnectDelay < 0) {
			throw new IllegalArgumentException("reconnectDelay cannot be a negative number");
		}
		this.reconnectDelay = reconnectDelay;
	}

	public long getPingDelay() {
		return pingDelay;
	}

	public void setPingDelay(long pingDelay) {
		if (pingDelay < 0) {
			throw new IllegalArgumentException("pingDelay cannot be a negative number");
		}
		this.pingDelay = pingDelay;
	}

	public long getEnableWorkerPacketDelay() {
		return enableWorkerPacketDelay;
	}

	public void setEnableWorkerPacketDelay(long enableWorkerPacketDelay) {
		if (enableWorkerPacketDelay < 0) {
			throw new IllegalArgumentException("EnableWorkerPacketDelay cannot be a negative number");
		}
		this.enableWorkerPacketDelay = enableWorkerPacketDelay;
	}

	public long getTimeoutMillis() {
		return timeoutMillis;
	}

	public void setTimeoutMillis(long timeoutMillis) {
		if (timeoutMillis < 0) {
			throw new IllegalArgumentException("timeoutMillis cannot be a negative number");
		}
		this.timeoutMillis = timeoutMillis;
	}

}

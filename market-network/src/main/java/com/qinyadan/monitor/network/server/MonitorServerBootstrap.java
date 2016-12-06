package com.qinyadan.monitor.network.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.MonitorSocketException;
import com.qinyadan.monitor.network.client.WriteFailFutureListener;
import com.qinyadan.monitor.network.cluster.ClusterOption;
import com.qinyadan.monitor.network.packet.PingPacket;
import com.qinyadan.monitor.network.stream.DisabledServerStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.ServerStreamChannelMessageListener;
import com.qinyadan.monitor.network.util.AssertUtils;
import com.qinyadan.monitor.network.util.CpuUtils;
import com.qinyadan.monitor.network.util.LoggerFactorySetup;
import com.qinyadan.monitor.network.util.TimerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

public class MonitorServerBootstrap implements MonitorServerConfig {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	static {
		LoggerFactorySetup.setupSlf4jLoggerFactory();
	}

	private static final long DEFAULT_TIMEOUTMILLIS = 3 * 1000;

	private static final int WORKER_COUNT = CpuUtils.workerCount();

	private volatile boolean released;

	private ServerBootstrap bootstrap;

	private InetAddress[] ignoreAddressList;

	private volatile Channel serverChannel;

	private final EventLoopGroup bossGroup;

	private final EventLoopGroup workerGroup;

	private ServerMessageListener messageListener = SimpleServerMessageListener.SIMPLEX_INSTANCE;

	private ServerStreamChannelMessageListener serverStreamChannelMessageListener = DisabledServerStreamChannelMessageListener.INSTANCE;

	private final Timer healthCheckTimer;

	private final Timer requestManagerTimer;

	private final ClusterOption clusterOption;

	private long defaultRequestTimeout = DEFAULT_TIMEOUTMILLIS;

	private final DefaultMonitorServer monitorServer;

	public MonitorServerBootstrap() throws InterruptedException {
		this(ClusterOption.DISABLE_CLUSTER_OPTION);
	}

	public MonitorServerBootstrap(ClusterOption clusterOption) throws InterruptedException {

		bossGroup = new NioEventLoopGroup(1,new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyBoss_%d", this.threadIndex.incrementAndGet()));
            }
        });
		workerGroup = new NioEventLoopGroup(WORKER_COUNT,new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            private int threadTotal = WORKER_COUNT;
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyServerEPOLLSelector_%d_%d",threadTotal, this.threadIndex.incrementAndGet()));
            }
        });
		
		this.healthCheckTimer = TimerFactory.createHashedWheelTimer("ServerSocket-HealthCheckTimer", 50,
				TimeUnit.MILLISECONDS, 512);
		this.requestManagerTimer = TimerFactory.createHashedWheelTimer("ServerSocket-RequestManager", 50,
				TimeUnit.MILLISECONDS, 512);
		this.clusterOption = clusterOption;
		this.monitorServer = new DefaultMonitorServer(this);
		
		ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.SO_SNDBUF, 1024 * 64)
				.childOption(ChannelOption.SO_RCVBUF, 1024 * 64)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ServerChannelInitializer(monitorServer));
		this.bootstrap = bootstrap;
	}

	public void bind(String host, int port) throws MonitorSocketException {
		InetSocketAddress bindAddress = new InetSocketAddress(host, port);
		bind(bindAddress);
	}

	public void bind(InetSocketAddress bindAddress) throws MonitorSocketException {
		if (released) {
			return;
		}
		logger.info("bind() {}", bindAddress);
		try {
			ChannelFuture sync = bootstrap.bind(bindAddress).sync();
			sync.channel().closeFuture().sync();
			this.serverChannel = sync.channel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendPing();
	}

	@Override
	public long getDefaultRequestTimeout() {
		return defaultRequestTimeout;
	}

	public void setDefaultRequestTimeout(long defaultRequestTimeout) {
		this.defaultRequestTimeout = defaultRequestTimeout;
	}

	private boolean isIgnoreAddress(Channel channel) {
		if (ignoreAddressList == null) {
			return false;
		}
		final InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
		if (remoteAddress == null) {
			return false;
		}
		InetAddress address = remoteAddress.getAddress();
		for (InetAddress ignore : ignoreAddressList) {
			if (ignore.equals(address)) {
				return true;
			}
		}
		return false;
	}

	public void setIgnoreAddressList(InetAddress[] ignoreAddressList) {
		AssertUtils.assertNotNull(ignoreAddressList, "ignoreAddressList must not be null");

		this.ignoreAddressList = ignoreAddressList;
	}

	@Override
	public ServerMessageListener getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(ServerMessageListener messageListener) {
		AssertUtils.assertNotNull(messageListener, "messageListener must not be null");
		this.messageListener = messageListener;
	}

	@Override
	public ServerStreamChannelMessageListener getStreamMessageListener() {
		return serverStreamChannelMessageListener;
	}

	public void setServerStreamChannelMessageListener(
			ServerStreamChannelMessageListener serverStreamChannelMessageListener) {
		AssertUtils.assertNotNull(serverStreamChannelMessageListener,
				"serverStreamChannelMessageListener must not be null");

		this.serverStreamChannelMessageListener = serverStreamChannelMessageListener;
	}

	@Override
	public Timer getHealthCheckTimer() {
		return healthCheckTimer;
	}

	@Override
	public Timer getRequestManagerTimer() {
		return requestManagerTimer;
	}

	@Override
	public ClusterOption getClusterOption() {
		return clusterOption;
	}

	private void sendPing() {
		final TimerTask pintTask = new TimerTask() {
			@Override
			public void run(Timeout timeout) throws Exception {
				if (timeout.isCancelled()) {
					newPingTimeout(this);
					return;
				}
				final ChannelFuture future = serverChannel.write(PingPacket.PING_PACKET);
				if (logger.isWarnEnabled()) {
					future.addListener(new ChannelFutureListener() {
						private final ChannelFutureListener listener = new WriteFailFutureListener(logger,
								"ping write fail", "ping write success");

						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							future.addListener(listener);
						}
					});
				}
				newPingTimeout(this);
			}
		};
		newPingTimeout(pintTask);
	}

	private void newPingTimeout(TimerTask pintTask) {
		try {
			healthCheckTimer.newTimeout(pintTask, 1000 * 60 * 5, TimeUnit.MILLISECONDS);
		} catch (IllegalStateException e) {
			// stop in case of timer stopped
			logger.debug("timer stopped. Caused:{}", e.getMessage());
		}
	}

	public void close() {
		synchronized (this) {
			if (released) {
				return;
			}
			released = true;
		}
		healthCheckTimer.stop();

		if (serverChannel != null) {
			ChannelFuture close = serverChannel.close();
			close.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS);
			serverChannel = null;
		}
		if (bootstrap != null) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

		// clear the request first and remove timer
		requestManagerTimer.stop();
	}

	private DefaultMonitorServer createPinpointServer(Channel channel) {
		DefaultMonitorServer pinpointServer = new DefaultMonitorServer(this);
		return pinpointServer;
	}

}

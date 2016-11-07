package com.qinyadan.brick.monitor.network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.config.ClientConfigManager;
import com.qinyadan.brick.monitor.spi.message.ext.MessageQueue;
import com.qinyadan.brick.monitor.utils.Pair;
import com.qinyadan.brick.monitor.utils.ServiceThread;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChannelManager extends  ServiceThread {
	
	private static final Logger logger  = LoggerFactory.getLogger(ChannelManager.class);

	private ClientConfigManager configManager;

	private Bootstrap bootstrap;

	

	private boolean active = true;

	private int retriedTimes = 0;

	private int count = -10;

	private volatile double sample = 1d;

	private MessageQueue queue;

	private ChannelHolder activeChannelHolder;

	public ChannelManager(List<InetSocketAddress> serverAddresses, MessageQueue queue,
			ClientConfigManager configManager) {
		this.queue = queue;
		this.configManager = configManager;

		EventLoopGroup group = new NioEventLoopGroup(1, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		});

		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
			}
		});

		String serverConfig = loadServerConfig();

		if (StringUtils.isNotEmpty(serverConfig)) {
			List<InetSocketAddress> configedAddresses = parseSocketAddress(serverConfig);
			ChannelHolder holder = initChannel(configedAddresses, serverConfig);

			if (holder != null) {
				activeChannelHolder = holder;
			} else {
				activeChannelHolder = new ChannelHolder();
				activeChannelHolder.setServerAddresses(configedAddresses);
			}
		} else {
			ChannelHolder holder = initChannel(serverAddresses, null);

			if (holder != null) {
				activeChannelHolder = holder;
			} else {
				activeChannelHolder = new ChannelHolder();
				activeChannelHolder.setServerAddresses(serverAddresses);
				logger.error("error when init cat module due to error config xml in /data/appdatas/cat/client.xml");
			}
		}
	}

	public ChannelFuture channel() {
		if (activeChannelHolder != null) {
			return activeChannelHolder.getActiveFuture();
		} else {
			return null;
		}
	}

	private void checkServerChanged() {
		if (shouldCheckServerConfig(++count)) {
			Pair<Boolean, String> pair = routerConfigChanged();

			if (pair.getObject1()) {
				String servers = pair.getObject2();
				List<InetSocketAddress> addresses = parseSocketAddress(servers);
				ChannelHolder newHolder = initChannel(addresses, servers);

				if (newHolder != null) {
					if (newHolder.isConnectChanged()) {
						ChannelHolder last = activeChannelHolder;

						activeChannelHolder = newHolder;
						closeChannelHolder(last);
						logger.info("switch active channel to " + activeChannelHolder);
					} else {
						activeChannelHolder = newHolder;
					}
				}
			}
		}
	}

	private void closeChannel(ChannelFuture channel) {
		try {
			if (channel != null) {
				logger.info("close channel " + channel.channel().remoteAddress());
				channel.channel().close();
			}
		} catch (Exception e) {
			// ignore
		}
	}

	private void closeChannelHolder(ChannelHolder channelHolder) {
		try {
			ChannelFuture channel = channelHolder.getActiveFuture();

			closeChannel(channel);
			channelHolder.setActiveIndex(-1);
		} catch (Exception e) {
			// ignore
		}
	}

	private ChannelFuture createChannel(InetSocketAddress address) {
		ChannelFuture future = null;

		try {
			future = bootstrap.connect(address);
			future.awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100 ms

			if (!future.isSuccess()) {
				logger.error("Error when try connecting to " + address);
				closeChannel(future);
			} else {
				logger.info("Connected to CAT server at " + address);
				return future;
			}
		} catch (Throwable e) {
			logger.error("Error when connect server " + address.getAddress(), e);

			if (future != null) {
				closeChannel(future);
			}
		}
		return null;
	}

	private void doubleCheckActiveServer(ChannelFuture activeFuture) {
		try {
			if (isChannelStalled(activeFuture) || isChannelDisabled(activeFuture)) {
				closeChannelHolder(activeChannelHolder);
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

	public double getSample() {
		return sample;
	}

	private ChannelHolder initChannel(List<InetSocketAddress> addresses, String serverConfig) {
		try {
			int len = addresses.size();

			for (int i = 0; i < len; i++) {
				InetSocketAddress address = addresses.get(i);
				String hostAddress = address.getHostName(); // address.getAddress().getHostAddress()
				ChannelHolder holder = null;

				if (activeChannelHolder != null && hostAddress.equals(activeChannelHolder.getIp())) {
					holder = new ChannelHolder();
					holder.setActiveFuture(activeChannelHolder.getActiveFuture()).setConnectChanged(false);
				} else {
					ChannelFuture future = createChannel(address);

					if (future != null) {
						holder = new ChannelHolder();
						holder.setActiveFuture(future).setConnectChanged(true);
					}
				}
				if (holder != null) {
					holder.setActiveIndex(i).setIp(hostAddress);
					holder.setActiveServerConfig(serverConfig).setServerAddresses(addresses);

					logger.info("success when init CAT server, new active holder" + holder.toString());
					return holder;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (addresses.size() > 0) {
			try {
				StringBuilder sb = new StringBuilder();

				for (InetSocketAddress address : addresses) {
					sb.append(address.toString()).append(";");
				}

				logger.info("Error when init CAT server " + sb.toString());
			} catch (Exception e) {
				// ignore
			}
		}

		return null;
	}

	private boolean isChannelDisabled(ChannelFuture activeFuture) {
		return activeFuture != null && !activeFuture.channel().isOpen();
	}

	private boolean isChannelStalled(ChannelFuture activeFuture) {
		retriedTimes++;

		int size = queue.size();
		boolean stalled = activeFuture != null && size >= TcpSocketSender.SIZE - 10;

		if (stalled) {
			if (retriedTimes >= 5) {
				retriedTimes = 0;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private String loadServerConfig() {
		try {
			/*
			 * String url = m_configManager.getServerConfigUrl(); InputStream
			 * inputstream =
			 * Urls.forIO().readTimeout(2000).connectTimeout(1000).openStream(
			 * url); String content = Files.forIO().readFrom(inputstream,
			 * "utf-8");
			 * 
			 * KVConfig routerConfig = (KVConfig)
			 * m_jsonBuilder.parse(content.trim(), KVConfig.class); String
			 * current = routerConfig.getValue("routers"); m_sample =
			 * Double.valueOf(routerConfig.getValue("sample").trim());
			 * 
			 * return current.trim();
			 */
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	private List<InetSocketAddress> parseSocketAddress(String content) {
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();

		try {
			List<String> strs = Arrays.asList(content.split(";"));

			for (String str : strs) {
				List<String> items = Arrays.asList(str.split(":"));
				String hostname = items.get(0);
				int port = Integer.parseInt(items.get(1));

				if (port > 0) {
					addresses.add(new InetSocketAddress(hostname, port));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return addresses;
	}

	private void reconnectDefaultServer(ChannelFuture activeFuture, List<InetSocketAddress> serverAddresses) {
		try {
			int reconnectServers = activeChannelHolder.getActiveIndex();

			if (reconnectServers == -1) {
				reconnectServers = serverAddresses.size();
			}
			for (int i = 0; i < reconnectServers; i++) {
				ChannelFuture future = createChannel(serverAddresses.get(i));

				if (future != null) {
					ChannelFuture lastFuture = activeFuture;

					activeChannelHolder.setActiveFuture(future);
					activeChannelHolder.setActiveIndex(i);
					closeChannel(lastFuture);
					break;
				}
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

	private Pair<Boolean, String> routerConfigChanged() {
		String current = loadServerConfig();

		if (!StringUtils.isEmpty(current) && !current.equals(activeChannelHolder.getActiveServerConfig())) {
			return new Pair<Boolean, String>(true, current);
		} else {
			return new Pair<Boolean, String>(false, current);
		}
	}

	@Override
	public void run() {
		while (active) {
			// make save message id index asyc
			checkServerChanged();

			ChannelFuture activeFuture = activeChannelHolder.getActiveFuture();
			List<InetSocketAddress> serverAddresses = activeChannelHolder.getServerAddresses();

			doubleCheckActiveServer(activeFuture);
			reconnectDefaultServer(activeFuture, serverAddresses);

			try {
				Thread.sleep(10 * 1000L); // check every 10 seconds
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private boolean shouldCheckServerConfig(int count) {
		int duration = 30;

		if (count % duration == 0 || activeChannelHolder.getActiveIndex() == -1) {
			return true;
		} else {
			return false;
		}
	}

	public class ClientMessageHandler extends SimpleChannelInboundHandler<Object> {

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
			logger.info("receiver msg from server:" + msg);
		}
	}

	public static class ChannelHolder {
		private ChannelFuture m_activeFuture;

		private int m_activeIndex = -1;

		private String m_activeServerConfig;

		private List<InetSocketAddress> m_serverAddresses;

		private String m_ip;

		private boolean m_connectChanged;

		public ChannelFuture getActiveFuture() {
			return m_activeFuture;
		}

		public int getActiveIndex() {
			return m_activeIndex;
		}

		public String getActiveServerConfig() {
			return m_activeServerConfig;
		}

		public String getIp() {
			return m_ip;
		}

		public List<InetSocketAddress> getServerAddresses() {
			return m_serverAddresses;
		}

		public boolean isConnectChanged() {
			return m_connectChanged;
		}

		public ChannelHolder setActiveFuture(ChannelFuture activeFuture) {
			m_activeFuture = activeFuture;
			return this;
		}

		public ChannelHolder setActiveIndex(int activeIndex) {
			m_activeIndex = activeIndex;
			return this;
		}

		public ChannelHolder setActiveServerConfig(String activeServerConfig) {
			m_activeServerConfig = activeServerConfig;
			return this;
		}

		public ChannelHolder setConnectChanged(boolean connectChanged) {
			m_connectChanged = connectChanged;
			return this;
		}

		public ChannelHolder setIp(String ip) {
			m_ip = ip;
			return this;
		}

		public ChannelHolder setServerAddresses(List<InetSocketAddress> serverAddresses) {
			m_serverAddresses = serverAddresses;
			return this;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("active future :").append(m_activeFuture.channel().remoteAddress());
			sb.append(" index:").append(m_activeIndex);
			sb.append(" ip:").append(m_ip);
			sb.append(" server config:").append(m_activeServerConfig);
			return sb.toString();
		}
	}

	@Override
	public String getServiceName() {
		return "TcpSender-ChannelManager";
	}

}
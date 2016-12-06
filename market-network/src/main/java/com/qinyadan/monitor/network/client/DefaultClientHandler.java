package com.qinyadan.monitor.network.client;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.ChannelWriteCompleteListenableFuture;
import com.qinyadan.monitor.network.ChannelWriteFailListenableFuture;
import com.qinyadan.monitor.network.Future;
import com.qinyadan.monitor.network.MessageListener;
import com.qinyadan.monitor.network.MonitorSocketException;
import com.qinyadan.monitor.network.ResponseMessage;
import com.qinyadan.monitor.network.client.ConnectFuture.Result;
import com.qinyadan.monitor.network.cluster.ClusterOption;
import com.qinyadan.monitor.network.common.SocketStateCode;
import com.qinyadan.monitor.network.packet.ClientClosePacket;
import com.qinyadan.monitor.network.packet.Packet;
import com.qinyadan.monitor.network.packet.PacketType;
import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.packet.ResponsePacket;
import com.qinyadan.monitor.network.packet.SendPacket;
import com.qinyadan.monitor.network.packet.stream.StreamPacket;
import com.qinyadan.monitor.network.server.SimpleServerMessageListener;
import com.qinyadan.monitor.network.stream.ClientStreamChannel;
import com.qinyadan.monitor.network.stream.ClientStreamChannelContext;
import com.qinyadan.monitor.network.stream.ClientStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.DisabledServerStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.ServerStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.StreamChannelContext;
import com.qinyadan.monitor.network.stream.StreamChannelManager;
import com.qinyadan.monitor.network.stream.StreamChannelStateChangeEventHandler;
import com.qinyadan.monitor.network.util.ClassUtils;
import com.qinyadan.monitor.network.util.IDGenerator;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 * @author liuzhimin
 *
 */
public class DefaultClientHandler extends SimpleChannelInboundHandler<Packet> implements DuplexClient {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final long DEFAULT_PING_DELAY = 60 * 1000 * 5;
	
	private static final long DEFAULT_TIMEOUTMILLIS = 3 * 1000;
	
	private static final long DEFAULT_ENABLE_WORKER_PACKET_DELAY = 60 * 1000 * 1;

	private final int socketId;

	// private final PinpointClientHandlerState state;

	private long timeoutMillis = DEFAULT_TIMEOUTMILLIS;

	private SocketAddress connectSocketAddress;

	private volatile MonitorClient monitorClient;

	private final MessageListener messageListener = SimpleServerMessageListener.SIMPLEX_INSTANCE;

	private final ServerStreamChannelMessageListener serverStreamChannelMessageListener;

	private final RequestManager requestManager;
	
	private final ChannelFutureListener pingWriteFailFutureListener = new WriteFailFutureListener(this.logger,
			"ping write fail.", "ping write success.");
	
	private final ChannelFutureListener sendWriteFailFutureListener = new WriteFailFutureListener(this.logger,
			"send() write fail.", "send() write success.");
	
	private final ChannelFutureListener sendClosePacketFailFutureListener = new WriteFailFutureListener(this.logger,
			"sendClosedPacket() write fail.", "sendClosedPacket() write success.");
	
	private final ConnectFuture connectFuture = new ConnectFuture();

	private final String objectUniqName;
	
	private ClusterOption remoteClusterOption = ClusterOption.DISABLE_CLUSTER_OPTION;

	private volatile Channel channel;

	private ClientHandlerContext context;

	public DefaultClientHandler(MonitorClientFactory clientFactory) {
		this(clientFactory, DEFAULT_PING_DELAY, DEFAULT_ENABLE_WORKER_PACKET_DELAY, DEFAULT_TIMEOUTMILLIS);
	}

	public DefaultClientHandler(MonitorClientFactory clientFactory, long pingDelay, long handshakeRetryInterval,
			long timeoutMillis) {

		this.requestManager = new RequestManager(timeoutMillis);
		this.timeoutMillis = timeoutMillis;
		this.serverStreamChannelMessageListener = DisabledServerStreamChannelMessageListener.INSTANCE;
		this.objectUniqName = ClassUtils.simpleClassNameAndHashCodeString(this);
		this.socketId = clientFactory.issueNewSocketId();
	}

	public void setPinpointClient(MonitorClient monitorClient) {
		if (monitorClient == null) {
			throw new NullPointerException("pinpointClient must not be null");
		}
		this.monitorClient = monitorClient;
	}

	public void setConnectSocketAddress(SocketAddress connectSocketAddress) {
		if (connectSocketAddress == null) {
			throw new NullPointerException("connectSocketAddress must not be null");
		}
		this.connectSocketAddress = connectSocketAddress;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		this.channel = channel;
		logger.info("{} channelConnected() started. channel:{}", objectUniqName, channel);
		prepareChannel(channel);
		connectFuture.setResult(Result.SUCCESS);
		logger.info("{} channelConnected() completed.", objectUniqName);
	}

	private void prepareChannel(Channel channel) {
		StreamChannelManager streamChannelManager = new StreamChannelManager(channel,IDGenerator.createOddIdGenerator(), serverStreamChannelMessageListener);
		context = new ClientHandlerContext(channel, streamChannelManager);
	}

	@Override
	public void initReconnect() {
		logger.info("{} initReconnect() started.", objectUniqName);
		logger.info("{} initReconnect() completed.", objectUniqName);
	}

	public void sendPing() {
		logger.debug("{} sendPing() started.", objectUniqName);

		logger.debug("{} sendPing() completed.", objectUniqName);
	}

	@Override
	public void send(byte[] bytes) {
		ChannelFuture future = send0(bytes);
		future.addListener(sendWriteFailFutureListener);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Future sendAsync(byte[] bytes) {
		ChannelFuture channelFuture = send0(bytes);
		final ChannelWriteCompleteListenableFuture future = new ChannelWriteCompleteListenableFuture(timeoutMillis);
		channelFuture.addListener(future);
		return future;
	}

	@Override
	public void sendSync(byte[] bytes) {
		ChannelFuture write = send0(bytes);
		await(write);
	}

	@Override
	public void response(int requestId, byte[] payload) {
		if (payload == null) {
			throw new NullPointerException("bytes");
		}
		ensureOpen();
		ResponsePacket response = new ResponsePacket(requestId, payload);
		write0(response);
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return connectSocketAddress;
	}

	private void await(ChannelFuture channelFuture) {
		try {
			channelFuture.await(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (channelFuture.isDone()) {
			boolean success = channelFuture.isSuccess();
			if (success) {
				return;
			} else {
				final Throwable cause = channelFuture.cause();
				throw new MonitorSocketException(cause);
			}
		} else {
			boolean cancel = channelFuture.isCancelled();
			if (cancel) {
				// if IO not finished in 3 seconds, dose it mean timeout?
				throw new MonitorSocketException("io timeout");
			} else {
				// same logic as above because of success
				boolean success = channelFuture.isSuccess();
				if (success) {
					return;
				} else {
					final Throwable cause = channelFuture.cause();
					throw new MonitorSocketException(cause);
				}
			}
		}
	}

	private ChannelFuture send0(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException("bytes");
		}

		ensureOpen();
		SendPacket send = new SendPacket(bytes);

		return write0(send);
	}

	public Future<ResponseMessage> request(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException("bytes");
		}
		RequestPacket request = new RequestPacket(bytes);
		final ChannelWriteFailListenableFuture<ResponseMessage> messageFuture = this.requestManager.register(request,
				this.timeoutMillis);
		write0(request, messageFuture);
		return messageFuture;
	}

	@Override
	public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener) {
		return openStream(payload, messageListener, null);
	}

	@Override
	public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener,
			StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener) {
		ensureOpen();
		return context.openStream(payload, messageListener, stateChangeListener);
	}

	@Override
	public StreamChannelContext findStreamChannel(int streamChannelId) {
		ensureOpen();
		return context.getStreamChannel(streamChannelId);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		logger.info("client recived message:{}"+new String(msg.getPayload()));
		if (msg instanceof Packet) {
			final Packet packet = (Packet) msg;
			final short packetType = packet.getPacketType();
			switch (packetType) {
			case PacketType.APPLICATION_RESPONSE:
				this.requestManager.messageReceived((ResponsePacket) msg, objectUniqName);
				return;
			// have to handle a request message through connector
			case PacketType.APPLICATION_REQUEST:
				// this.messageListener.handleRequest((RequestPacket) msg,
				// pinpointClient);
				return;
			case PacketType.APPLICATION_SEND:
				// this.messageListener.handleSend((SendPacket) msg,
				// pinpointClient);
				return;
			case PacketType.APPLICATION_STREAM_CREATE:
			case PacketType.APPLICATION_STREAM_CLOSE:
			case PacketType.APPLICATION_STREAM_CREATE_SUCCESS:
			case PacketType.APPLICATION_STREAM_CREATE_FAIL:
			case PacketType.APPLICATION_STREAM_RESPONSE:
			case PacketType.APPLICATION_STREAM_PING:
			case PacketType.APPLICATION_STREAM_PONG:
				context.handleStreamEvent((StreamPacket) msg);
				return;
			case PacketType.CONTROL_SERVER_CLOSE:
				handleClosedPacket(ctx.channel());
				return;
			case PacketType.CONTROL_HANDSHAKE_RESPONSE:
				// handleHandshakePacket((ControlHandshakeResponsePacket)msg,
				// ctx.channel());
				return;
			default:
				logger.warn("{} messageReceived() failed. unexpectedMessage received:{} address:{}", objectUniqName,
						msg, ctx.channel().remoteAddress());
			}
		} else {
			logger.warn("{} messageReceived() failed. invalid messageReceived:{}", objectUniqName, msg);
		}
	}

	private void handleClosedPacket(Channel channel) {
		logger.info("{} handleClosedPacket() started. channel:{}", objectUniqName, channel);

		// state.toBeingCloseByPeer();
	}

	

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("异常" + cause);
	}

	private void ensureOpen() {
	}

	// Calling this method on a closed PinpointClientHandler has no effect.
	public void close() {
		logger.debug("{} close() started.", objectUniqName);
	}

	private void closeChannel() {
		Channel channel = this.channel;
		if (channel != null) {
			sendClosedPacket(channel);

			ChannelFuture closeFuture = channel.close();
			closeFuture.addListener(
					new WriteFailFutureListener(logger, "close() event failed.", "close() event success."));
			closeFuture.awaitUninterruptibly();
		}
	}

	// Calling this method on a closed PinpointClientHandler has no effect.
	private void closeResources() {
		logger.debug("{} closeResources() started.", objectUniqName);

		Channel channel = this.channel;
		closeStreamChannelManager(channel);
		this.requestManager.close();
	}

	private void closeStreamChannelManager(Channel channel) {
		if (channel == null) {
			logger.debug("channel already set null. skip closeStreamChannelManager().");
			return;
		}

		if (context != null) {
			context.closeAllStreamChannel();
		}
	}

	private void sendClosedPacket(Channel channel) {
		if (!channel.isOpen()) {
			logger.debug("{} sendClosedPacket() failed. Error:channel already closed.", objectUniqName);
			return;
		}

		logger.debug("{} sendClosedPacket() started.", objectUniqName);

		ClientClosePacket clientClosePacket = new ClientClosePacket();
		ChannelFuture write = write0(clientClosePacket, sendClosePacketFailFutureListener);
		write.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		logger.info("{} channelClosed() started.", objectUniqName);
		try {
			
		} finally {
			closeResources();
			connectFuture.setResult(Result.FAIL);
		}
	}

	private ChannelFuture write0(Object message) {
		return write0(message, null);
	}

	private ChannelFuture write0(Object message, ChannelFutureListener futureListener) {
		ChannelFuture future = channel.writeAndFlush(message);
		if (futureListener != null) {
			future.addListener(futureListener);
		}
		return future;
	}

	@Override
	public ConnectFuture getConnectFuture() {
		return connectFuture;
	}

	@Override
	public SocketStateCode getCurrentStateCode() {
		return null;// state.getCurrentStateCode();
	}

	@Override
	public boolean isConnected() {
		return true;// this.state.isEnableCommunication();
	}

	@Override
	public boolean isSupportServerMode() {
		return true; // messageListener != SimpleMessageListener.INSTANCE;
	}

	@Override
	public ClusterOption getLocalClusterOption() {
		return null;// localClusterOption;
	}

	@Override
	public ClusterOption getRemoteClusterOption() {
		return remoteClusterOption;
	}

	protected MonitorClient getPinpointClient() {
		return monitorClient;
	}

	protected String getObjectName() {
		return objectUniqName;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(objectUniqName);
		sb.append('{');
		sb.append("channel=").append(channel);
		// sb.append("state=").append(state);
		sb.append("state=").append("status");
		sb.append('}');
		return sb.toString();
	}

}
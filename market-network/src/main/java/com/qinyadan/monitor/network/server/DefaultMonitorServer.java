package com.qinyadan.monitor.network.server;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.ChannelWriteFailListenableFuture;
import com.qinyadan.monitor.network.Future;
import com.qinyadan.monitor.network.ResponseMessage;
import com.qinyadan.monitor.network.client.RequestManager;
import com.qinyadan.monitor.network.client.WriteFailFutureListener;
import com.qinyadan.monitor.network.cluster.ClusterOption;
import com.qinyadan.monitor.network.cluster.Role;
import com.qinyadan.monitor.network.control.ProtocolException;
import com.qinyadan.monitor.network.packet.ControlHandshakePacket;
import com.qinyadan.monitor.network.packet.ControlHandshakeResponsePacket;
import com.qinyadan.monitor.network.packet.HandshakeResponseCode;
import com.qinyadan.monitor.network.packet.Packet;
import com.qinyadan.monitor.network.packet.PacketType;
import com.qinyadan.monitor.network.packet.PingPacket;
import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.packet.ResponsePacket;
import com.qinyadan.monitor.network.packet.SendPacket;
import com.qinyadan.monitor.network.packet.ServerClosePacket;
import com.qinyadan.monitor.network.packet.stream.StreamPacket;
import com.qinyadan.monitor.network.stream.ClientStreamChannel;
import com.qinyadan.monitor.network.stream.ClientStreamChannelContext;
import com.qinyadan.monitor.network.stream.ClientStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.StreamChannelContext;
import com.qinyadan.monitor.network.stream.StreamChannelManager;
import com.qinyadan.monitor.network.stream.StreamChannelStateChangeEventHandler;
import com.qinyadan.monitor.network.util.AssertUtils;
import com.qinyadan.monitor.network.util.ClassUtils;
import com.qinyadan.monitor.network.util.ControlMessageEncodingUtils;
import com.qinyadan.monitor.network.util.IDGenerator;
import com.qinyadan.monitor.network.util.MapUtils;
import com.qinyadan.monitor.network.util.StringUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class DefaultMonitorServer implements MonitorServer {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Channel channel;

	private final RequestManager requestManager;

	private final ServerMessageListener messageListener;

	private final StreamChannelManager streamChannelManager;

	private final AtomicReference<Map<Object, Object>> properties = new AtomicReference<Map<Object, Object>>();

	private final String objectUniqName;

	private final ClusterOption localClusterOption;

	private ClusterOption remoteClusterOption;

	private final ChannelFutureListener serverCloseWriteListener;

	private final ChannelFutureListener responseWriteFailListener;

	private final WriteFailFutureListener pongWriteFutureListener = new WriteFailFutureListener(logger,
			"pong write fail.", "pong write success.");

	public DefaultMonitorServer(MonitorServerConfig serverConfig) {

		this.messageListener = serverConfig.getMessageListener();

		StreamChannelManager streamChannelManager = new StreamChannelManager(channel,
				IDGenerator.createEvenIdGenerator(), serverConfig.getStreamMessageListener());
		this.streamChannelManager = streamChannelManager;

		RequestManager requestManager = new RequestManager(serverConfig.getDefaultRequestTimeout());
		this.requestManager = requestManager;

		this.objectUniqName = ClassUtils.simpleClassNameAndHashCodeString(this);

		this.serverCloseWriteListener = new WriteFailFutureListener(logger,
				objectUniqName + " sendClosePacket() write fail.", "serverClosePacket write success");
		this.responseWriteFailListener = new WriteFailFutureListener(logger,
				objectUniqName + " response() write fail.");

		this.localClusterOption = serverConfig.getClusterOption();
	}

	@Override
	public void start() {
		logger.info("{} start() started. channel:{}.", objectUniqName, channel);

		logger.info("{} start() completed.", objectUniqName);
	}

	public void stop() {
		logger.info("{} stop() started. channel:{}.", objectUniqName, channel);
		stop(false);
		logger.info("{} stop() completed.", objectUniqName);
	}

	public void stop(boolean serverStop) {
		try {
			if (this.channel.isOpen()) {
				channel.close();
			}
		} finally {
			streamChannelManager.close();
		}
	}

	@Override
	public void send(byte[] payload) {
		AssertUtils.assertNotNull(payload, "payload may not be null.");
		SendPacket send = new SendPacket(payload);
		write0(send);
	}

	@Override
	public Future<ResponseMessage> request(byte[] payload) {
		AssertUtils.assertNotNull(payload, "payload may not be null.");

		RequestPacket requestPacket = new RequestPacket(payload);
		ChannelWriteFailListenableFuture<ResponseMessage> messageFuture = this.requestManager.register(requestPacket);
		write0(requestPacket, messageFuture);
		return messageFuture;
	}

	@Override
	public void response(RequestPacket requestPacket, byte[] payload) {
		response(requestPacket.getRequestId(), payload);
	}

	@Override
	public void response(int requestId, byte[] payload) {
		AssertUtils.assertNotNull(payload, "payload may not be null.");
		ResponsePacket responsePacket = new ResponsePacket(requestId, payload);
		write0(responsePacket, responseWriteFailListener);
	}

	private ChannelFuture write0(Object message) {
		return write0(message, null);
	}

	private ChannelFuture write0(Object message, ChannelFutureListener futureListener) {
		ChannelFuture future = channel.write(message);
		if (futureListener != null) {
			future.addListener(futureListener);
		}
		return future;
	}

	public StreamChannelContext getStreamChannel(int channelId) {
		return streamChannelManager.findStreamChannel(channelId);
	}

	@Override
	public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener) {
		return openStream(payload, messageListener, null);
	}

	@Override
	public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener,
			StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener) {
		logger.info("{} createStream() started.", objectUniqName);

		ClientStreamChannelContext streamChannel = streamChannelManager.openStream(payload, messageListener,
				stateChangeListener);

		logger.info("{} createStream() completed.", objectUniqName);
		return streamChannel;
	}

	public void closeAllStreamChannel() {
		logger.info("{} closeAllStreamChannel() started.", objectUniqName);

		streamChannelManager.close();

		logger.info("{} closeAllStreamChannel() completed.", objectUniqName);
	}

	@Override
	public Map<Object, Object> getChannelProperties() {
		Map<Object, Object> properties = this.properties.get();
		return properties == null ? Collections.emptyMap() : properties;
	}

	public boolean setChannelProperties(Map<Object, Object> value) {
		if (value == null) {
			return false;
		}

		return this.properties.compareAndSet(null, Collections.unmodifiableMap(value));
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return channel.remoteAddress();
	}

	public ChannelFuture sendClosePacket() {
		logger.info("{} sendClosePacket() started.", objectUniqName);

		if (this.channel.isOpen()) {
			final ChannelFuture writeFuture = this.channel.write(ServerClosePacket.DEFAULT_SERVER_CLOSE_PACKET);
			writeFuture.addListener(serverCloseWriteListener);

			logger.info("{} sendClosePacket() completed.", objectUniqName);
			return writeFuture;
		} else {
			logger.info("{} sendClosePacket() failed. Error:{}.", objectUniqName, this.channel.isActive());
			return null;
		}
	}

	@Override
	public void messageReceived(Object message) {

		final short packetType = getPacketType(message);
		switch (packetType) {
		case PacketType.APPLICATION_SEND: {
			handleSend((SendPacket) message);
			return;
		}
		case PacketType.APPLICATION_REQUEST: {
			handleRequest((RequestPacket) message);
			return;
		}
		case PacketType.APPLICATION_RESPONSE: {
			handleResponse((ResponsePacket) message);
			return;
		}
		case PacketType.APPLICATION_STREAM_CREATE:
		case PacketType.APPLICATION_STREAM_CLOSE:
		case PacketType.APPLICATION_STREAM_CREATE_SUCCESS:
		case PacketType.APPLICATION_STREAM_CREATE_FAIL:
		case PacketType.APPLICATION_STREAM_RESPONSE:
		case PacketType.APPLICATION_STREAM_PING:
		case PacketType.APPLICATION_STREAM_PONG:
			handleStreamEvent((StreamPacket) message);
			return;
		case PacketType.CONTROL_HANDSHAKE:
			handleHandshake((ControlHandshakePacket) message);
			return;
		case PacketType.CONTROL_CLIENT_CLOSE: {
			handleClosePacket(channel);
			return;
		}
		case PacketType.CONTROL_PING: {
			handlePingPacket(channel, (PingPacket) message);
			return;
		}
		default: {
			logger.warn("invalid messageReceived msg:{}, connection:{}", message, channel);
		}
		}
	}

	private short getPacketType(Object packet) {
		if (packet == null) {
			return PacketType.UNKNOWN;
		}

		if (packet instanceof Packet) {
			return ((Packet) packet).getPacketType();
		}

		return PacketType.UNKNOWN;
	}

	private void handleSend(SendPacket sendPacket) {
		messageListener.handleSend(sendPacket, this);
	}

	private void handleRequest(RequestPacket requestPacket) {
		messageListener.handleRequest(requestPacket, this);
	}

	private void handleResponse(ResponsePacket responsePacket) {
		this.requestManager.messageReceived(responsePacket, this);
	}

	private void handleStreamEvent(StreamPacket streamPacket) {
		streamChannelManager.messageReceived(streamPacket);
	}

	private void handleHandshake(ControlHandshakePacket handshakepacket) {
		logger.info("{} handleHandshake() started. Packet:{}", objectUniqName, handshakepacket);

		int requestId = handshakepacket.getRequestId();

		logger.info("{} handleHandshake() completed.", objectUniqName);
	}

	private ClusterOption getClusterOption(Map handshakeResponse) {
		if (handshakeResponse == Collections.EMPTY_MAP) {
			return ClusterOption.DISABLE_CLUSTER_OPTION;
		}

		Map cluster = (Map) handshakeResponse.get(ControlHandshakeResponsePacket.CLUSTER);
		if (cluster == null) {
			return ClusterOption.DISABLE_CLUSTER_OPTION;
		}

		String id = MapUtils.getString(cluster, "id", "");
		List<Role> roles = getRoles((List) cluster.get("roles"));

		if (StringUtils.isEmpty(id)) {
			return ClusterOption.DISABLE_CLUSTER_OPTION;
		} else {
			return new ClusterOption(true, id, roles);
		}
	}

	private List<Role> getRoles(List roleNames) {
		List<Role> roles = new ArrayList<Role>();
		for (Object roleName : roleNames) {
			if (roleName instanceof String && !StringUtils.isEmpty((String) roleName)) {
				roles.add(Role.getValue((String) roleName));
			}
		}
		return roles;
	}

	private void handleClosePacket(Channel channel) {
		logger.info("{} handleClosePacket() started.", objectUniqName);

	}

	private void handlePingPacket(Channel channel, PingPacket packet) {
		logger.debug("{} handlePingPacket() started. packet:{}", objectUniqName, packet);

	}

	private Map<String, Object> createHandshakeResponse(HandshakeResponseCode responseCode, boolean isFirst) {
		HandshakeResponseCode createdCode = null;
		if (isFirst) {
			createdCode = responseCode;
		} else {
			if (HandshakeResponseCode.DUPLEX_COMMUNICATION == responseCode) {
				createdCode = HandshakeResponseCode.ALREADY_DUPLEX_COMMUNICATION;
			} else if (HandshakeResponseCode.SIMPLEX_COMMUNICATION == responseCode) {
				createdCode = HandshakeResponseCode.ALREADY_SIMPLEX_COMMUNICATION;
			} else {
				createdCode = responseCode;
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(ControlHandshakeResponsePacket.CODE, createdCode.getCode());
		result.put(ControlHandshakeResponsePacket.SUB_CODE, createdCode.getSubCode());
		if (localClusterOption.isEnable()) {
			result.put(ControlHandshakeResponsePacket.CLUSTER, localClusterOption.getProperties());
		}

		return result;
	}

	private void sendHandshakeResponse0(int requestId, Map<String, Object> data) {
		try {
			byte[] resultPayload = ControlMessageEncodingUtils.encode(data);
			ControlHandshakeResponsePacket packet = new ControlHandshakeResponsePacket(requestId, resultPayload);

			channel.write(packet);
		} catch (ProtocolException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	private Map<Object, Object> decodeHandshakePacket(ControlHandshakePacket message) {
		try {
			byte[] payload = message.getPayload();
			Map<Object, Object> properties = (Map) ControlMessageEncodingUtils.decode(payload);
			return properties;
		} catch (ProtocolException e) {
			logger.warn(e.getMessage(), e);
		}

		return Collections.EMPTY_MAP;
	}

	String getObjectUniqName() {
		return objectUniqName;
	}

	@Override
	public ClusterOption getLocalClusterOption() {
		return localClusterOption;
	}

	@Override
	public ClusterOption getRemoteClusterOption() {
		return remoteClusterOption;
	}

	@Override
	public void close() {
		stop();
	}

	@Override
	public String toString() {
		StringBuilder log = new StringBuilder(32);
		log.append(objectUniqName);
		log.append("(");
		log.append("remote:");
		log.append(getRemoteAddress());
		log.append(")");

		return log.toString();
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public StreamChannelManager getStreamChannelManager() {
		return streamChannelManager;
	}

}

package com.qinyadan.monitor.network.client;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.DefaultFuture;
import com.qinyadan.monitor.network.Future;
import com.qinyadan.monitor.network.DuplexSocket;
import com.qinyadan.monitor.network.MonitorSocketException;
import com.qinyadan.monitor.network.ResponseMessage;
import com.qinyadan.monitor.network.cluster.ClusterOption;
import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.stream.ClientStreamChannel;
import com.qinyadan.monitor.network.stream.ClientStreamChannelContext;
import com.qinyadan.monitor.network.stream.ClientStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.StreamChannelContext;
import com.qinyadan.monitor.network.stream.StreamChannelStateChangeEventHandler;
import com.qinyadan.monitor.network.util.AssertUtils;


/**
 * 在连接到服务端后返回一个 client 对象
 * 
 * @author liuzhimin
 *
 */
public class MonitorClient implements DuplexSocket {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private volatile DuplexClient duplexClient;

	private volatile boolean closed;

	private List<ClientReconnectEventListener> reconnectEventListeners = new CopyOnWriteArrayList<ClientReconnectEventListener>();

	public MonitorClient() {
	}

	public MonitorClient(DuplexClient duplexClient) {
		AssertUtils.assertNotNull(duplexClient, "clientHandler");
		this.duplexClient = duplexClient;
		duplexClient.setPinpointClient(this);
	}

	void reconnectSocketHandler(DuplexClient duplexClient) {
		AssertUtils.assertNotNull(duplexClient, "clientHandler");
		if (closed) {
			logger.warn("reconnectClientHandler(). clientHandler force close.");
			duplexClient.close();
			return;
		}
		logger.warn("reconnectClientHandler:{}", duplexClient);

		this.duplexClient = duplexClient;

		notifyReconnectEvent();
	}

	/*
	 * because reconnectEventListener's constructor contains Dummy and can't be
	 * access through setter, guarantee it is not null.
	 */
	public boolean addPinpointClientReconnectEventListener(ClientReconnectEventListener eventListener) {
		if (eventListener == null) {
			return false;
		}

		return this.reconnectEventListeners.add(eventListener);
	}

	public boolean removePinpointClientReconnectEventListener(ClientReconnectEventListener eventListener) {
		if (eventListener == null) {
			return false;
		}

		return this.reconnectEventListeners.remove(eventListener);
	}

	private void notifyReconnectEvent() {
		for (ClientReconnectEventListener eachListener : this.reconnectEventListeners) {
			eachListener.reconnectPerformed(this);
		}
	}

	public void sendSync(byte[] bytes) {
		ensureOpen();
		duplexClient.sendSync(bytes);
	}

	@SuppressWarnings("rawtypes")
	public Future sendAsync(byte[] bytes) {
		ensureOpen();
		return duplexClient.sendAsync(bytes);
	}

	@Override
	public void send(byte[] bytes) {
		ensureOpen();
		duplexClient.send(bytes);
	}

	@Override
	public Future<ResponseMessage> request(byte[] bytes) {
		if (duplexClient == null) {
			return returnFailureFuture();
		}
		return duplexClient.request(bytes);
	}

	@Override
	public void response(RequestPacket requestPacket, byte[] payload) {
		response(requestPacket.getRequestId(), payload);
	}

	@Override
	public void response(int requestId, byte[] payload) {
		ensureOpen();
		duplexClient.response(requestId, payload);
	}

	@Override
	public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener) {
		return openStream(payload, messageListener, null);
	}

	@Override
	public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener,
			StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener) {
		ensureOpen();
		return duplexClient.openStream(payload, messageListener, stateChangeListener);
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return duplexClient.getRemoteAddress();
	}

	@Override
	public ClusterOption getLocalClusterOption() {
		return duplexClient.getLocalClusterOption();
	}

	@Override
	public ClusterOption getRemoteClusterOption() {
		return duplexClient.getRemoteClusterOption();
	}

	public StreamChannelContext findStreamChannel(int streamChannelId) {

		ensureOpen();
		return duplexClient.findStreamChannel(streamChannelId);
	}

	private Future<ResponseMessage> returnFailureFuture() {
		DefaultFuture<ResponseMessage> future = new DefaultFuture<ResponseMessage>();
		future.setFailure(new MonitorSocketException("clientHandler is null"));
		return future;
	}

	private void ensureOpen() {
		if (duplexClient == null) {
			throw new MonitorSocketException("clientHandler is null");
		}
	}

	/**
	 * write ping packet on tcp channel PinpointSocketException throws when
	 * writing fails.
	 *
	 */
	public void sendPing() {
		DuplexClient duplexClient = this.duplexClient;
		if (duplexClient == null) {
			return;
		}
		duplexClient.sendPing();
	}

	@Override
	public void close() {
		synchronized (this) {
			if (closed) {
				return;
			}
			closed = true;
		}
		DuplexClient duplexClient = this.duplexClient;
		if (duplexClient == null) {
			return;
		}
		duplexClient.close();
	}

	public boolean isClosed() {
		return closed;
	}

	public boolean isConnected() {
		return this.duplexClient.isConnected();
	}
}

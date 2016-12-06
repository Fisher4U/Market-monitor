package com.qinyadan.monitor.network.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.ChannelWriteFailListenableFuture;
import com.qinyadan.monitor.network.DefaultFuture;
import com.qinyadan.monitor.network.FailureEventHandler;
import com.qinyadan.monitor.network.MonitorSocketException;
import com.qinyadan.monitor.network.ResponseMessage;
import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.packet.ResponsePacket;
import com.qinyadan.monitor.network.server.MonitorServer;

import io.netty.channel.Channel;

public class RequestManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final AtomicInteger requestId = new AtomicInteger(1);

	private final ConcurrentMap<Integer, DefaultFuture<ResponseMessage>> requestMap = new ConcurrentHashMap<Integer, DefaultFuture<ResponseMessage>>();

	private final long defaultTimeoutMillis;

	public RequestManager(long defaultTimeoutMillis) {

		if (defaultTimeoutMillis <= 0) {
			throw new IllegalArgumentException("defaultTimeoutMillis must greater than zero.");
		}

		this.defaultTimeoutMillis = defaultTimeoutMillis;
	}

	private FailureEventHandler createFailureEventHandler(final int requestId) {
		FailureEventHandler failureEventHandler = new FailureEventHandler() {
			@Override
			public boolean fireFailure() {
				DefaultFuture<ResponseMessage> future = removeMessageFuture(requestId);
				if (future != null) {
					// removed perfectly.
					return true;
				}
				return false;
			}
		};
		return failureEventHandler;
	}

	private int getNextRequestId() {
		return this.requestId.getAndIncrement();
	}

	public void messageReceived(ResponsePacket responsePacket, String objectUniqName) {
		final int requestId = responsePacket.getRequestId();
		final DefaultFuture<ResponseMessage> future = removeMessageFuture(requestId);
		if (future == null) {
			logger.warn("future not found:{}, objectUniqName:{}", responsePacket, objectUniqName);
			return;
		} else {
			logger.debug("responsePacket arrived packet:{}, objectUniqName:{}", responsePacket, objectUniqName);
		}

		ResponseMessage response = new ResponseMessage();
		response.setMessage(responsePacket.getPayload());
		future.setResult(response);
	}

	public void messageReceived(ResponsePacket responsePacket, MonitorServer monitorServer) {
		final int requestId = responsePacket.getRequestId();
		final DefaultFuture<ResponseMessage> future = removeMessageFuture(requestId);
		if (future == null) {
			logger.warn("future not found:{}, pinpointServer:{}", responsePacket, monitorServer);
			return;
		} else {
			logger.debug("responsePacket arrived packet:{}, pinpointServer:{}", responsePacket, monitorServer);
		}

		ResponseMessage response = new ResponseMessage();
		response.setMessage(responsePacket.getPayload());
		future.setResult(response);
	}

	public DefaultFuture<ResponseMessage> removeMessageFuture(int requestId) {
		return this.requestMap.remove(requestId);
	}

	public void messageReceived(RequestPacket requestPacket, Channel channel) {
		logger.error("unexpectedMessage received:{} address:{}", requestPacket, channel.remoteAddress());
	}

	public ChannelWriteFailListenableFuture<ResponseMessage> register(RequestPacket requestPacket) {
		return register(requestPacket, defaultTimeoutMillis);
	}

	public ChannelWriteFailListenableFuture<ResponseMessage> register(RequestPacket requestPacket, long timeoutMillis) {
		// shutdown check
		final int requestId = getNextRequestId();
		requestPacket.setRequestId(requestId);

		final ChannelWriteFailListenableFuture<ResponseMessage> future = new ChannelWriteFailListenableFuture<ResponseMessage>(
				timeoutMillis);

		final DefaultFuture<?> old = this.requestMap.put(requestId, future);
		if (old != null) {
			throw new MonitorSocketException("unexpected error. old future exist:" + old + " id:" + requestId);
		}

		// when future fails, put a handle in order to remove a failed future in
		// the requestMap.
		FailureEventHandler removeTable = createFailureEventHandler(requestId);
		future.setFailureEventHandler(removeTable);

		return future;
	}

	public void close() {
		logger.debug("close()");
		final MonitorSocketException closed = new MonitorSocketException("socket closed");

		// Could you handle race conditions of "close" more precisely?
		// final Timer timer = this.timer;
		// if (timer != null) {
		// Set<Timeout> stop = timer.stop();
		// for (Timeout timeout : stop) {
		// DefaultFuture future = (DefaultFuture)timeout.getTask();
		// future.setFailure(closed);
		// }
		// }
		int requestFailCount = 0;
		for (Map.Entry<Integer, DefaultFuture<ResponseMessage>> entry : requestMap.entrySet()) {
			if (entry.getValue().setFailure(closed)) {
				requestFailCount++;
			}
		}
		this.requestMap.clear();
		if (requestFailCount > 0) {
			logger.info("requestManager failCount:{}", requestFailCount);
		}

	}

}

package com.qinyadan.brick.monitor.spi.message.ext.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.qinyadan.brick.monitor.spi.message.Event;
import com.qinyadan.brick.monitor.spi.message.Heartbeat;
import com.qinyadan.brick.monitor.spi.message.Message;
import com.qinyadan.brick.monitor.spi.message.Metric;
import com.qinyadan.brick.monitor.spi.message.Transaction;
import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;
import com.qinyadan.brick.monitor.spi.message.internal.MessageId;

import io.netty.buffer.ByteBuf;

public class DefaultMessageTree implements MessageTree {

	private ByteBuf buf;

	private String domain;

	private String hostName;

	private String ipAddress;

	private Message message;

	private String messageId;

	private MessageId formatMessageId;

	private String parentMessageId;

	private String rootMessageId;

	private String sessionToken;

	private String threadGroupName;

	private String threadId;

	private String threadName;

	private boolean sample;

	private List<Transaction> transactions = new ArrayList<Transaction>();

	private List<Event> events = new ArrayList<Event>();

	private List<Heartbeat> heartbeats;

	private List<Metric> metrics;

	@Override
	public MessageTree copy() {
		MessageTree tree = new DefaultMessageTree();

		tree.setDomain(domain);
		tree.setHostName(hostName);
		tree.setIpAddress(ipAddress);
		tree.setMessageId(messageId);
		tree.setParentMessageId(parentMessageId);
		tree.setRootMessageId(rootMessageId);
		tree.setSessionToken(sessionToken);
		tree.setThreadGroupName(threadGroupName);
		tree.setThreadId(threadId);
		tree.setThreadName(threadName);
		tree.setMessage(message);
		tree.setSample(sample);

		return tree;
	}

	@Override
	public String getDomain() {
		return domain;
	}

	public List<Event> getEvents() {
		return events;
	}

	@Override
	public MessageId getFormatMessageId() {
		return formatMessageId;
	}

	public List<Heartbeat> getHeartbeats() {
		if (heartbeats == null) {
			return Collections.emptyList();
		} else {
			return heartbeats;
		}
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	@Override
	public Message getMessage() {
		return message;
	}

	@Override
	public String getMessageId() {
		return messageId;
	}

	public List<Metric> getMetrics() {
		if (metrics == null) {
			return Collections.emptyList();
		} else {
			return metrics;
		}
	}

	@Override
	public String getParentMessageId() {
		return parentMessageId;
	}

	@Override
	public String getRootMessageId() {
		return rootMessageId;
	}

	@Override
	public String getSessionToken() {
		return sessionToken;
	}

	@Override
	public String getThreadGroupName() {
		return threadGroupName;
	}

	@Override
	public String getThreadId() {
		return threadId;
	}

	@Override
	public String getThreadName() {
		return threadName;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	@Override
	public boolean isSample() {
		return sample;
	}

	@Override
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public void setFormatMessageId(MessageId messageId) {
		formatMessageId = messageId;
	}

	@Override
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public void setMessage(Message message) {
		this.message = message;
	}

	@Override
	public void setMessageId(String messageId) {
		if (messageId != null && messageId.length() > 0) {
			this.messageId = messageId;
		}
	}

	@Override
	public void setParentMessageId(String parentMessageId) {
		if (parentMessageId != null && parentMessageId.length() > 0) {
			this.parentMessageId = parentMessageId;
		}
	}

	@Override
	public void setRootMessageId(String rootMessageId) {
		if (rootMessageId != null && rootMessageId.length() > 0) {
			this.rootMessageId = rootMessageId;
		}
	}

	@Override
	public void setSample(boolean sample) {
		this.sample = sample;
	}

	@Override
	public void setSessionToken(String sessionToken) {
		if (sessionToken != null && sessionToken.length() > 0) {
			this.sessionToken = sessionToken;
		}
	}

	@Override
	public void setThreadGroupName(String threadGroupName) {
		this.threadGroupName = threadGroupName;
	}

	@Override
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	@Override
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public void addMetric(Metric metric) {
		if (metrics == null) {
			metrics = new ArrayList<Metric>();
		}

		metrics.add(metric);
	}

	public ByteBuf getBuffer() {
		return buf;
	}

	public void setBuffer(ByteBuf buf) {
		this.buf = buf;
	}

	public void addHeartbeat(Heartbeat heartbeat) {
		if (heartbeats == null) {
			heartbeats = new ArrayList<Heartbeat>();
		}

		heartbeats.add(heartbeat);
	}

	@Override
	public String toString() {
		return "DefaultMessageTree [domain=" + domain + ", hostName=" + hostName + ", ipAddress=" + ipAddress
				+ ", message=" + message + ", messageId=" + messageId + ", formatMessageId=" + formatMessageId
				+ ", parentMessageId=" + parentMessageId + ", rootMessageId=" + rootMessageId + ", sessionToken="
				+ sessionToken + ", threadGroupName=" + threadGroupName + ", threadId=" + threadId + ", threadName="
				+ threadName + ", sample=" + sample + ", transactions=" + transactions + ", events=" + events
				+ ", heartbeats=" + heartbeats + ", metrics=" + metrics + "]";
	}
	
}

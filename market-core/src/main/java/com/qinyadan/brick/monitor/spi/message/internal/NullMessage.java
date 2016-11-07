package com.qinyadan.brick.monitor.spi.message.internal;

import java.util.Collections;
import java.util.List;

import com.qinyadan.brick.monitor.spi.message.Event;
import com.qinyadan.brick.monitor.spi.message.Heartbeat;
import com.qinyadan.brick.monitor.spi.message.Message;
import com.qinyadan.brick.monitor.spi.message.Metric;
import com.qinyadan.brick.monitor.spi.message.Trace;
import com.qinyadan.brick.monitor.spi.message.Transaction;
import com.qinyadan.brick.monitor.spi.message.ext.ForkedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.TaggedTransaction;

public enum NullMessage implements Transaction, Event, Metric, Trace, Heartbeat, ForkedTransaction, TaggedTransaction {
	TRANSACTION,

	EVENT,

	METRIC,

	TRACE,

	HEARTBEAT;

	@Override
	public Transaction addChild(Message message) {
		return this;
	}

	@Override
	public void addData(String keyValuePairs) {
	}

	@Override
	public void addData(String key, Object value) {
	}

	@Override
	public void bind(String tag, String childMessageId, String title) {
	}

	@Override
	public void complete() {
	}

	@Override
	public void fork() {
	}

	@Override
	public List<Message> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public long getDurationInMicros() {
		return 0;
	}

	@Override
	public long getDurationInMillis() {
		return 0;
	}

	@Override
	public String getForkedMessageId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	public String getParentMessageId() {
		return null;
	}

	public String getRootMessageId() {
		return null;
	}

	@Override
	public String getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTag() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getTimestamp() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

	@Override
	public boolean isStandalone() {
		return true;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public void setStatus(String status) {
	}

	@Override
	public void setStatus(Throwable e) {
	}

	@Override
	public void start() {
	}
}

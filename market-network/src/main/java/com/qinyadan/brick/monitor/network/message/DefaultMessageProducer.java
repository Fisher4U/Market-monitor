package com.qinyadan.brick.monitor.network.message;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.qinyadan.brick.monitor.spi.message.Event;
import com.qinyadan.brick.monitor.spi.message.Heartbeat;
import com.qinyadan.brick.monitor.spi.message.Message;
import com.qinyadan.brick.monitor.spi.message.MessageFactory;
import com.qinyadan.brick.monitor.spi.message.Metric;
import com.qinyadan.brick.monitor.spi.message.Trace;
import com.qinyadan.brick.monitor.spi.message.Transaction;
import com.qinyadan.brick.monitor.spi.message.ext.ForkedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;
import com.qinyadan.brick.monitor.spi.message.ext.TaggedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.support.DefaultTaggedTransaction;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultEvent;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultHeartbeat;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultMetric;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultTrace;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultTransaction;
import com.qinyadan.brick.monitor.spi.message.internal.MessageManager;
import com.qinyadan.brick.monitor.spi.message.internal.NullMessage;
import com.qinyadan.brick.monitor.utils.MessageIdFactory;

public class DefaultMessageProducer implements MessageFactory {

	private MessageManager manager;

	private MessageIdFactory factory;

	@Override
	public String createMessageId() {
		return factory.getNextId();
	}

	@Override
	public boolean isEnabled() {
		return manager.isMessageEnabled();
	}

	@Override
	public void logError(String message, Throwable cause) {
		if (Cat.getManager().isCatEnabled()) {
			if (shouldLog(cause)) {
				manager.getThreadLocalMessageTree().setSample(false);

				StringWriter writer = new StringWriter(2048);

				if (message != null) {
					writer.write(message);
					writer.write(' ');
				}

				cause.printStackTrace(new PrintWriter(writer));

				String detailMessage = writer.toString();

				if (cause instanceof Error) {
					logEvent("Error", cause.getClass().getName(), "ERROR", detailMessage);
				} else if (cause instanceof RuntimeException) {
					logEvent("RuntimeException", cause.getClass().getName(), "ERROR", detailMessage);
				} else {
					logEvent("Exception", cause.getClass().getName(), "ERROR", detailMessage);
				}
			}
		} else {
			cause.printStackTrace();
		}
	}

	@Override
	public void logError(Throwable cause) {
		logError(null, cause);
	}

	@Override
	public void logEvent(String type, String name) {
		logEvent(type, name, Message.SUCCESS, null);
	}

	@Override
	public void logEvent(String type, String name, String status, String nameValuePairs) {
		Event event = newEvent(type, name);

		if (nameValuePairs != null && nameValuePairs.length() > 0) {
			event.addData(nameValuePairs);
		}

		event.setStatus(status);
		event.complete();
	}

	@Override
	public void logHeartbeat(String type, String name, String status, String nameValuePairs) {
		Heartbeat heartbeat = newHeartbeat(type, name);

		heartbeat.addData(nameValuePairs);
		heartbeat.setStatus(status);
		heartbeat.complete();
	}

	@Override
	public void logMetric(String name, String status, String nameValuePairs) {
		String type = "";
		Metric metric = newMetric(type, name);

		if (nameValuePairs != null && nameValuePairs.length() > 0) {
			metric.addData(nameValuePairs);
		}

		metric.setStatus(status);
		metric.complete();
	}

	@Override
	public void logTrace(String type, String name) {
		logTrace(type, name, Message.SUCCESS, null);
	}

	@Override
	public void logTrace(String type, String name, String status, String nameValuePairs) {
		if (manager.isTraceMode()) {
			Trace trace = newTrace(type, name);

			if (nameValuePairs != null && nameValuePairs.length() > 0) {
				trace.addData(nameValuePairs);
			}

			trace.setStatus(status);
			trace.complete();
		}
	}

	@Override
	public Event newEvent(String type, String name) {
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled()) {
			DefaultEvent event = new DefaultEvent(type, name, manager);

			return event;
		} else {
			return NullMessage.EVENT;
		}
	}

	public Event newEvent(Transaction parent, String type, String name) {
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled() && parent != null) {
			DefaultEvent event = new DefaultEvent(type, name);

			parent.addChild(event);
			return event;
		} else {
			return NullMessage.EVENT;
		}
	}

	@Override
	public ForkedTransaction newForkedTransaction(String type, String name) {
		// this enable CAT client logging cat message without explicit setup
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled()) {
			MessageTree tree = manager.getThreadLocalMessageTree();

			if (tree.getMessageId() == null) {
				tree.setMessageId(createMessageId());
			}

			DefaultForkedTransaction transaction = new DefaultForkedTransaction(type, name, manager);

			if (manager instanceof DefaultMessageManager) {
				((DefaultMessageManager) manager).linkAsRunAway(transaction);
			}
			manager.start(transaction, true);
			return transaction;
		} else {
			return NullMessage.TRANSACTION;
		}
	}

	@Override
	public Heartbeat newHeartbeat(String type, String name) {
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled()) {
			DefaultHeartbeat heartbeat = new DefaultHeartbeat(type, name, manager);

			manager.getThreadLocalMessageTree().setSample(false);
			return heartbeat;
		} else {
			return NullMessage.HEARTBEAT;
		}
	}

	@Override
	public Metric newMetric(String type, String name) {
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled()) {
			DefaultMetric metric = new DefaultMetric(type == null ? "" : type, name, manager);

			manager.getThreadLocalMessageTree().setSample(false);
			return metric;
		} else {
			return NullMessage.METRIC;
		}
	}

	@Override
	public TaggedTransaction newTaggedTransaction(String type, String name, String tag) {
		// this enable CAT client logging cat message without explicit setup
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled()) {
			MessageTree tree = manager.getThreadLocalMessageTree();

			if (tree.getMessageId() == null) {
				tree.setMessageId(createMessageId());
			}
			DefaultTaggedTransaction transaction = new DefaultTaggedTransaction(type, name, tag, manager);

			manager.start(transaction, true);
			return transaction;
		} else {
			return NullMessage.TRANSACTION;
		}
	}

	@Override
	public Trace newTrace(String type, String name) {
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled()) {
			DefaultTrace trace = new DefaultTrace(type, name, manager);

			return trace;
		} else {
			return NullMessage.TRACE;
		}
	}

	@Override
	public Transaction newTransaction(String type, String name) {
		// this enable CAT client logging cat message without explicit setup
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled()) {
			DefaultTransaction transaction = new DefaultTransaction(type, name, manager);

			manager.start(transaction, false);
			return transaction;
		} else {
			return NullMessage.TRANSACTION;
		}
	}

	public Transaction newTransaction(Transaction parent, String type, String name) {
		// this enable CAT client logging cat message without explicit setup
		if (!manager.hasContext()) {
			manager.setup();
		}

		if (manager.isMessageEnabled() && parent != null) {
			DefaultTransaction transaction = new DefaultTransaction(type, name, manager);

			parent.addChild(transaction);
			transaction.setStandalone(false);
			return transaction;
		} else {
			return NullMessage.TRANSACTION;
		}
	}

	private boolean shouldLog(Throwable e) {
		if (manager instanceof DefaultMessageManager) {
			return ((DefaultMessageManager) manager).shouldLog(e);
		} else {
			return true;
		}
	}
}

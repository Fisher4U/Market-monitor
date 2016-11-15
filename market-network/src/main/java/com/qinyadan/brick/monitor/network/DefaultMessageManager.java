package com.qinyadan.brick.monitor.network;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.config.ClientConfigManager;
import com.qinyadan.brick.monitor.domain.Domain;
import com.qinyadan.brick.monitor.spi.message.Message;
import com.qinyadan.brick.monitor.spi.message.Transaction;
import com.qinyadan.brick.monitor.spi.message.ext.ForkedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;
import com.qinyadan.brick.monitor.spi.message.ext.TaggedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.support.DefaultForkedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.support.DefaultMessageTree;
import com.qinyadan.brick.monitor.spi.message.ext.support.DefaultTaggedTransaction;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultEvent;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultTransaction;
import com.qinyadan.brick.monitor.spi.message.internal.MessageManager;
import com.qinyadan.brick.monitor.utils.MessageIdFactory;
import com.qinyadan.brick.monitor.utils.NetworkInterfaceManager;

public class DefaultMessageManager implements MessageManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultMessageManager.class);

	private ClientConfigManager configManager;

	private TransportManager transportManager;

	private MessageIdFactory factory = new MessageIdFactory();

	// we don't use static modifier since MessageManager is configured as
	// singleton
	private ThreadLocal<Context> context = new ThreadLocal<Context>();

	private long throttleTimes;

	private Domain domain;

	private String hostName;

	private boolean firstMessage = true;

	private TransactionHelper validator = new TransactionHelper();

	private Map<String, TaggedTransaction> taggedTransactions;

	public DefaultMessageManager(ClientConfigManager configManager) {
		this.configManager = configManager;
		this.domain = configManager.getDomain();
		transportManager = new DefaultTransportManager(configManager);
		
		hostName = NetworkInterfaceManager.INSTANCE.getLocalHostName();

		if (domain.getIp() == null) {
			domain.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
		}

		// initialize domain and IP address
		try {
			factory.initialize(new File("/data/appdatas/cat/"), domain.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// initialize the tagged transaction cache
		final int size = configManager.getTaggedTransactionCacheSize();

		taggedTransactions = new LinkedHashMap<String, TaggedTransaction>(size * 4 / 3 + 1, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Entry<String, TaggedTransaction> eldest) {
				return size() >= size;
			}
		};
	}

	@Override
	public void add(Message message) {
		Context ctx = getContext();

		if (ctx != null) {
			ctx.add(message);
		}
	}

	@Override
	public void bind(String tag, String title) {
		TaggedTransaction t = taggedTransactions.get(tag);

		if (t != null) {
			MessageTree tree = getThreadLocalMessageTree();
			String messageId = tree.getMessageId();

			if (messageId == null) {
				messageId = nextMessageId();
				tree.setMessageId(messageId);
			}
			if (tree != null) {
				t.start();
				t.bind(tag, messageId, title);
			}
		}
	}

	@Override
	public void end(Transaction transaction) {
		Context ctx = getContext();

		if (ctx != null && transaction.isStandalone()) {
			if (ctx.end(this, transaction)) {
				context.remove();
			}
		}
	}

	public void flush(MessageTree tree) {
		if (tree.getMessageId() == null) {
			tree.setMessageId(nextMessageId());
		}

		MessageSender sender = transportManager.getSender();

		if (sender != null && isMessageEnabled()) {
			sender.send(tree);

			reset();
		} else {
			throttleTimes++;

			if (throttleTimes % 10000 == 0 || throttleTimes == 1) {
				logger.info("Monitor Message is throttled! Times:" + throttleTimes);
			}
		}
	}

	public ClientConfigManager getConfigManager() {
		return configManager;
	}

	private Context getContext() {
		if (Monitor.isInitialized()) {
			Context ctx = context.get();

			if (ctx != null) {
				return ctx;
			} else {
				if (domain != null) {
					ctx = new Context(domain.getId(), hostName, domain.getIp());
				} else {
					ctx = new Context("Unknown", hostName, "");
				}

				context.set(ctx);
				return ctx;
			}
		}

		return null;
	}

	@Override
	public String getDomain() {
		return domain.getId();
	}

	public String getMetricType() {
		return "";
	}

	@Override
	public Transaction getPeekTransaction() {
		Context ctx = getContext();

		if (ctx != null) {
			return ctx.peekTransaction(this);
		} else {
			return null;
		}
	}

	@Override
	public MessageTree getThreadLocalMessageTree() {
		Context ctx = context.get();

		if (ctx == null) {
			setup();
		}
		ctx = context.get();

		return ctx.tree;
	}

	@Override
	public boolean hasContext() {
		return context.get() != null;
	}

	@Override
	public boolean isCatEnabled() {
		return domain != null && domain.isEnabled() && configManager.isCatEnabled();
	}

	@Override
	public boolean isMessageEnabled() {
		return domain != null && domain.isEnabled() && context.get() != null && configManager.isCatEnabled();
	}

	public boolean isTraceMode() {
		Context content = getContext();

		if (content != null) {
			return content.isTraceMode();
		} else {
			return false;
		}
	}

	public void linkAsRunAway(DefaultForkedTransaction transaction) {
		Context ctx = getContext();
		if (ctx != null) {
			ctx.linkAsRunAway(transaction);
		}
	}

	public String nextMessageId() {
		return factory.getNextId();
	}

	@Override
	public void reset() {
		// destroy current thread local data
		Context ctx = context.get();

		if (ctx != null) {
			if (ctx.totalDurationInMicros == 0) {
				ctx.stack.clear();
				ctx.knownExceptions.clear();
				context.remove();
			} else {
				ctx.knownExceptions.clear();
			}
		}
	}

	public void setMetricType(String metricType) {
	}

	public void setTraceMode(boolean traceMode) {
		Context context = getContext();

		if (context != null) {
			context.setTraceMode(traceMode);
		}
	}

	@Override
	public void setup() {
		Context ctx;

		if (domain != null) {
			ctx = new Context(domain.getId(), hostName, domain.getIp());
		} else {
			ctx = new Context("Unknown", hostName, "");
		}

		context.set(ctx);
	}

	boolean shouldLog(Throwable e) {
		Context ctx = context.get();

		if (ctx != null) {
			return ctx.shouldLog(e);
		} else {
			return true;
		}
	}

	@Override
	public void start(Transaction transaction, boolean forked) {
		Context ctx = getContext();

		if (ctx != null) {
			ctx.start(transaction, forked);

			if (transaction instanceof TaggedTransaction) {
				TaggedTransaction tt = (TaggedTransaction) transaction;

				taggedTransactions.put(tt.getTag(), tt);
			}
		} else if (firstMessage) {
			firstMessage = false;
			logger.warn("CAT client is not enabled because it's not initialized yet");
		}
	}

	class Context {
		private MessageTree tree;

		private Stack<Transaction> stack;

		private int length;

		private boolean traceMode;

		private long totalDurationInMicros; // for truncate message

		private Set<Throwable> knownExceptions;

		public Context(String domain, String hostName, String ipAddress) {
			tree = new DefaultMessageTree();
			stack = new Stack<Transaction>();

			Thread thread = Thread.currentThread();
			String groupName = thread.getThreadGroup().getName();

			tree.setThreadGroupName(groupName);
			tree.setThreadId(String.valueOf(thread.getId()));
			tree.setThreadName(thread.getName());

			tree.setDomain(domain);
			tree.setHostName(hostName);
			tree.setIpAddress(ipAddress);
			length = 1;
			knownExceptions = new HashSet<Throwable>();
		}

		public void add(Message message) {
			if (stack.isEmpty()) {
				MessageTree _tree = tree.copy();

				_tree.setMessage(message);
				flush(_tree);
			} else {
				Transaction parent = stack.peek();

				addTransactionChild(message, parent);
			}
		}

		private void addTransactionChild(Message message, Transaction transaction) {
			long treePeriod = trimToHour(tree.getMessage().getTimestamp());
			long messagePeriod = trimToHour(message.getTimestamp() - 10 * 1000L); // 10
																					// seconds
																					// extra
																					// time
																					// allowed

			if (treePeriod < messagePeriod || length >= configManager.getMaxMessageLength()) {
				validator.truncateAndFlush(this, message.getTimestamp());
			}

			transaction.addChild(message);
			length++;
		}

		private void adjustForTruncatedTransaction(Transaction root) {
			DefaultEvent next = new DefaultEvent("TruncatedTransaction", "TotalDuration");
			long actualDurationInMicros = totalDurationInMicros + root.getDurationInMicros();

			next.addData(String.valueOf(actualDurationInMicros));
			next.setStatus(Message.SUCCESS);
			root.addChild(next);

			totalDurationInMicros = 0;
		}

		/**
		 * return true means the transaction has been flushed.
		 * 
		 * @param manager
		 * @param transaction
		 * @return true if message is flushed, false otherwise
		 */
		public boolean end(DefaultMessageManager manager, Transaction transaction) {
			if (!stack.isEmpty()) {
				Transaction current = stack.pop();

				if (transaction == current) {
					validator.validate(stack.isEmpty() ? null : stack.peek(), current);
				} else {
					while (transaction != current && !stack.empty()) {
						validator.validate(stack.peek(), current);

						current = stack.pop();
					}
				}

				if (stack.isEmpty()) {
					MessageTree _tree = tree.copy();

					_tree.setMessageId(null);
					_tree.setMessage(null);

					if (totalDurationInMicros > 0) {
						adjustForTruncatedTransaction((Transaction) _tree.getMessage());
					}

					manager.flush(_tree);
					return true;
				}
			}

			return false;
		}

		public boolean isTraceMode() {
			return traceMode;
		}

		public void linkAsRunAway(DefaultForkedTransaction transaction) {
			validator.linkAsRunAway(transaction);
		}

		public Transaction peekTransaction(DefaultMessageManager defaultMessageManager) {
			if (stack.isEmpty()) {
				return null;
			} else {
				return stack.peek();
			}
		}

		public void setTraceMode(boolean traceMode) {
			traceMode = traceMode;
		}

		public boolean shouldLog(Throwable e) {
			if (knownExceptions == null) {
				knownExceptions = new HashSet<Throwable>();
			}

			if (knownExceptions.contains(e)) {
				return false;
			} else {
				knownExceptions.add(e);
				return true;
			}
		}

		public void start(Transaction transaction, boolean forked) {
			if (!stack.isEmpty()) {
				// Do NOT make strong reference from parent transaction to
				// forked transaction.
				// Instead, we create a "soft" reference to forked transaction
				// later, via linkAsRunAway()
				// By doing so, there is no need for synchronization between
				// parent and child threads.
				// Both threads can complete() anytime despite the other thread.
				if (!(transaction instanceof ForkedTransaction)) {
					Transaction parent = stack.peek();
					addTransactionChild(transaction, parent);
				}
			} else {
				tree.setMessage(transaction);
			}

			if (!forked) {
				stack.push(transaction);
			}
		}

		private long trimToHour(long timestamp) {
			return timestamp - timestamp % (3600 * 1000L);
		}
	}

	class TransactionHelper {
		private void linkAsRunAway(DefaultForkedTransaction transaction) {
			DefaultEvent event = new DefaultEvent("RemoteCall", "RunAway");

			event.addData(transaction.getForkedMessageId(), transaction.getType() + ":" + transaction.getName());
			event.setTimestamp(transaction.getTimestamp());
			event.setStatus(Message.SUCCESS);
			event.setCompleted(true);
			transaction.setStandalone(true);

			add(event);
		}

		private void markAsNotCompleted(DefaultTransaction transaction) {
			DefaultEvent event = new DefaultEvent("cat", "BadInstrument");

			event.setStatus("TransactionNotCompleted");
			event.setCompleted(true);
			transaction.addChild(event);
			transaction.setCompleted(true);
		}

		private void markAsRunAway(Transaction parent, DefaultTaggedTransaction transaction) {
			if (!transaction.hasChildren()) {
				transaction.addData("RunAway");
			}

			transaction.setStatus(Message.SUCCESS);
			transaction.setStandalone(true);
			transaction.complete();
		}

		private void migrateMessage(Stack<Transaction> stack, Transaction source, Transaction target, int level) {
			Transaction current = level < stack.size() ? stack.get(level) : null;
			boolean shouldKeep = false;

			for (Message child : source.getChildren()) {
				if (child != current) {
					target.addChild(child);
				} else {
					DefaultTransaction cloned = new DefaultTransaction(current.getType(), current.getName(),
							DefaultMessageManager.this);

					cloned.setTimestamp(current.getTimestamp());
					cloned.setDurationInMicros(current.getDurationInMicros());
					cloned.addData(current.getData().toString());
					cloned.setStatus(Message.SUCCESS);

					target.addChild(cloned);
					migrateMessage(stack, current, cloned, level + 1);
					shouldKeep = true;
				}
			}

			source.getChildren().clear();

			if (shouldKeep) { // add it back
				source.addChild(current);
			}
		}

		public void truncateAndFlush(Context ctx, long timestamp) {
			MessageTree tree = ctx.tree;
			Stack<Transaction> stack = ctx.stack;
			Message message = tree.getMessage();

			if (message instanceof DefaultTransaction) {
				String id = tree.getMessageId();

				if (id == null) {
					id = nextMessageId();
					tree.setMessageId(id);
				}

				String rootId = tree.getRootMessageId();
				String childId = nextMessageId();
				DefaultTransaction source = (DefaultTransaction) message;
				DefaultTransaction target = new DefaultTransaction(source.getType(), source.getName(),
						DefaultMessageManager.this);

				target.setTimestamp(source.getTimestamp());
				target.setDurationInMicros(source.getDurationInMicros());
				target.addData(source.getData().toString());
				target.setStatus(Message.SUCCESS);

				migrateMessage(stack, source, target, 1);

				for (int i = stack.size() - 1; i >= 0; i--) {
					DefaultTransaction t = (DefaultTransaction) stack.get(i);

					t.setTimestamp(timestamp);
					t.setDurationStart(System.nanoTime());
				}

				DefaultEvent next = new DefaultEvent("RemoteCall", "Next");

				next.addData(childId);
				next.setStatus(Message.SUCCESS);
				target.addChild(next);

				// tree is the parent, and tree is the child.
				MessageTree t = tree.copy();

				t.setMessage(target);

				ctx.tree.setMessageId(childId);
				ctx.tree.setParentMessageId(id);
				ctx.tree.setRootMessageId(rootId != null ? rootId : id);

				ctx.length = stack.size();
				ctx.totalDurationInMicros = ctx.totalDurationInMicros + target.getDurationInMicros();

				flush(t);
			}
		}

		public void validate(Transaction parent, Transaction transaction) {
			if (transaction.isStandalone()) {
				List<Message> children = transaction.getChildren();
				int len = children.size();

				for (int i = 0; i < len; i++) {
					Message message = children.get(i);

					if (message instanceof Transaction) {
						validate(transaction, (Transaction) message);
					}
				}

				if (!transaction.isCompleted() && transaction instanceof DefaultTransaction) {
					// missing transaction end, log a BadInstrument event so
					// that
					// developer can fix the code
					markAsNotCompleted((DefaultTransaction) transaction);
				}
			} else if (!transaction.isCompleted()) {
				if (transaction instanceof DefaultForkedTransaction) {
					// link it as run away message since the forked transaction
					// is not completed yet
					linkAsRunAway((DefaultForkedTransaction) transaction);
				} else if (transaction instanceof DefaultTaggedTransaction) {
					// link it as run away message since the forked transaction
					// is not completed yet
					markAsRunAway(parent, (DefaultTaggedTransaction) transaction);
				}
			}
		}
	}
}

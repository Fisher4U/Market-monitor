package com.qinyadan.brick.monitor.network;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.config.ClientConfigManager;
import com.qinyadan.brick.monitor.network.codec.MessageCodec;
import com.qinyadan.brick.monitor.network.codec.NativeMessageCodec;
import com.qinyadan.brick.monitor.spi.message.Message;
import com.qinyadan.brick.monitor.spi.message.Transaction;
import com.qinyadan.brick.monitor.spi.message.ext.MessageQueue;
import com.qinyadan.brick.monitor.spi.message.ext.MessageStatistics;
import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;
import com.qinyadan.brick.monitor.spi.message.ext.support.DefaultMessageTree;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultTransaction;
import com.qinyadan.brick.monitor.utils.ServiceThread;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class TcpSocketSender extends ServiceThread implements MessageSender {

	private static final Logger logger = LoggerFactory.getLogger(TcpSocketSender.class);

	public static final String ID = "tcp-socket-sender";

	public static final int SIZE = 5000;

	private MessageCodec codec;

	private MessageStatistics statistics;

	private ClientConfigManager configManager;

	private MessageQueue queue = new DefaultMessageQueue(SIZE);

	private MessageQueue atomicTrees = new DefaultMessageQueue(SIZE);

	private List<InetSocketAddress> serverAddresses;

	private ChannelManager manager;

	private transient boolean active;

	private AtomicInteger errors = new AtomicInteger();

	private AtomicInteger attempts = new AtomicInteger();

	private static final int MAX_CHILD_NUMBER = 200;

	private ExecutorService sendExecutor;

	private ExecutorService manageExecutor;

	private ExecutorService mergeExecutor;

	@Override
	public void initialize() {
		manager = new ChannelManager(serverAddresses, queue, configManager);

		this.sendExecutor = Executors.newFixedThreadPool(2, new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "TCPsendExecutor_" + this.threadIndex.incrementAndGet());
			}
		});
		this.manageExecutor = Executors.newFixedThreadPool(1, new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "TCPmanageExecutor_" + this.threadIndex.incrementAndGet());
			}
		});

		this.mergeExecutor = Executors.newFixedThreadPool(4, new ThreadFactory() {
			private AtomicInteger threadIndex = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "TCPmergeExecutor_" + this.threadIndex.incrementAndGet());
			}
		});
		codec = new NativeMessageCodec();
		sendExecutor.execute(this);
		manageExecutor.execute(manager);
		mergeExecutor.execute(new MergeAtomicTask());
	}

	private boolean checkWritable(ChannelFuture future) {
		boolean isWriteable = false;
		Channel channel = future.channel();

		if (future != null && channel.isOpen()) {
			if (channel.isActive() && channel.isWritable()) {
				isWriteable = true;
			} else {
				int count = attempts.incrementAndGet();

				if (count % 1000 == 0 || count == 1) {
					logger.error("Netty write buffer is full! Attempts: " + count);
				}
			}
		}

		return isWriteable;
	}

	private boolean isAtomicMessage(MessageTree tree) {
		Message message = tree.getMessage();

		if (message instanceof Transaction) {
			String type = message.getType();

			if (type.startsWith("Cache.") || "SQL".equals(type)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private void logQueueFullInfo(MessageTree tree) {
		if (statistics != null) {
			statistics.onOverflowed(tree);
		}

		int count = errors.incrementAndGet();

		if (count % 1000 == 0 || count == 1) {
			logger.error("Message queue is full in tcp socket sender! Count: " + count);
		}

		tree = null;
	}

	private MessageTree mergeTree(MessageQueue trees) {
		int max = MAX_CHILD_NUMBER;
		DefaultTransaction tran = new DefaultTransaction("_MarketMergeTree", "_MarketMergeTree", null);
		MessageTree first = trees.poll();

		tran.setStatus(Transaction.SUCCESS);
		tran.setCompleted(true);
		tran.addChild(first.getMessage());
		tran.setTimestamp(first.getMessage().getTimestamp());
		long lastTimestamp = 0;
		long lastDuration = 0;

		while (max >= 0) {
			MessageTree tree = trees.poll();

			if (tree == null) {
				tran.setDurationInMillis(lastTimestamp - tran.getTimestamp() + lastDuration);
				break;
			}
			lastTimestamp = tree.getMessage().getTimestamp();
			if (tree.getMessage() instanceof DefaultTransaction) {
				lastDuration = ((DefaultTransaction) tree.getMessage()).getDurationInMillis();
			} else {
				lastDuration = 0;
			}
			tran.addChild(tree.getMessage());
			max--;
		}
		((DefaultMessageTree) first).setMessage(tran);
		return first;
	}

	@Override
	public void run() {
		active = true;

		while (active) {
			ChannelFuture channel = manager.channel();

			if (channel != null && checkWritable(channel)) {
				try {
					MessageTree tree = queue.poll();

					if (tree != null) {
						sendInternal(tree);
						tree.setMessage(null);
					}

				} catch (Throwable t) {
					logger.error("Error when sending message over TCP socket!", t);
				}
			} else {
				try {
					Thread.sleep(5);
				} catch (Exception e) {
					// ignore it
					active = false;
				}
			}
		}
	}

	@Override
	public void send(MessageTree tree) {
		if (isAtomicMessage(tree)) {
			boolean result = atomicTrees.offer(tree, manager.getSample());

			if (!result) {
				logQueueFullInfo(tree);
			}
		} else {
			boolean result = queue.offer(tree, manager.getSample());

			if (!result) {
				logQueueFullInfo(tree);
			}
		}
	}

	private void sendInternal(MessageTree tree) {
		ChannelFuture future = manager.channel();
		ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(10 * 1024); // 10K

		buf.writeInt(0); // placeholder of length

		codec.encode(tree, buf);

		int size = buf.readableBytes();

		buf.setInt(0, size - 4); // length

		Channel channel = future.channel();

		channel.writeAndFlush(buf);

		if (statistics != null) {
			statistics.onBytes(size);
		}
	}

	public void setServerAddresses(List<InetSocketAddress> serverAddresses) {
		this.serverAddresses = serverAddresses;
	}

	private boolean shouldMerge(MessageQueue trees) {
		MessageTree tree = trees.peek();

		if (tree != null) {
			long firstTime = tree.getMessage().getTimestamp();
			int maxDuration = 1000 * 30;

			if (System.currentTimeMillis() - firstTime > maxDuration || trees.size() >= MAX_CHILD_NUMBER) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		active = false;
		manager.shutdown();
	}

	public class MergeAtomicTask extends ServiceThread {

		@Override
		public void run() {
			while (true) {
				if (shouldMerge(atomicTrees)) {
					MessageTree tree = mergeTree(atomicTrees);
					boolean result = queue.offer(tree);

					if (!result) {
						logQueueFullInfo(tree);
					}
				} else {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}

		@Override
		public String getServiceName() {
			return "merge-atomic-task";
		}
	}

	@Override
	public String getServiceName() {
		return "TcpSocketSender";
	}

}

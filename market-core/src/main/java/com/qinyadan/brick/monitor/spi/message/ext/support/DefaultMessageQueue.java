package com.qinyadan.brick.monitor.spi.message.ext.support;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.qinyadan.brick.monitor.spi.message.ext.MessageQueue;
import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;

public class DefaultMessageQueue implements MessageQueue {

	private BlockingQueue<MessageTree> queue;

	private AtomicInteger count = new AtomicInteger();

	public DefaultMessageQueue(int size) {
		queue = new ArrayBlockingQueue<MessageTree>(size);
	}

	@Override
	public boolean offer(MessageTree tree) {
		return queue.offer(tree);
	}

	@Override
	public boolean offer(MessageTree tree, double sampleRatio) {
		if (tree.isSample() && sampleRatio < 1.0) {
			if (sampleRatio > 0) {
				int _count = count.incrementAndGet();

				if (_count % (1 / sampleRatio) == 0) {
					return offer(tree);
				}
			}
			return false;
		} else {
			return offer(tree);
		}
	}

	@Override
	public MessageTree peek() {
		return queue.peek();
	}

	@Override
	public MessageTree poll() {
		try {
			return queue.poll(5, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}

	@Override
	public int size() {
		return queue.size();
	}
}

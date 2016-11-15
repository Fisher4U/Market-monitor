package com.qinyadan.brick.monitor.network;

import java.util.Map;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;


/**
 * Message analyzer is responsible of processing each message in the queue, and producing a report normally.
 * <p>
 *
 * For each hour, there is one instance will be instantiated and be assigned with a specific queue.
 */
public interface MessageAnalyzer extends Runnable {
	
	public boolean handle(MessageTree tree);

	public void configure(Map<String, String> properties);

	public void doCheckpoint(boolean atEnd) throws Exception;

	public String[] getDependencies();

	public void initialize(int index, int hour) throws Exception;

	public void destroy();
}

package com.qinyadan.brick.monitor.network;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;

public interface MessageConsumer {
	
	public void consume(MessageTree tree);
	
	public void doCheckpoint();
}

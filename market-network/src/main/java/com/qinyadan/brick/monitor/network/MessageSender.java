package com.qinyadan.brick.monitor.network;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;

public interface MessageSender {
	
	public void initialize();

	public void send(MessageTree tree);

	public void shutdown();
}

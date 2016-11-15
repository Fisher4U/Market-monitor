package com.qinyadan.brick.monitor.network;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;

public interface MessageFilter {
	public boolean apply(MessageTree tree);
}

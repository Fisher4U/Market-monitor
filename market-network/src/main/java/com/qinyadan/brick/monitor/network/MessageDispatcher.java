package com.qinyadan.brick.monitor.network;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;

public interface MessageDispatcher {
	public void dispatch(MessageTree tree);
}

package com.qinyadan.brick.monitor.network;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;

public interface Pipeline {
	public void initialize(int hour);

	public void destroy();

	public boolean analyze(MessageTree tree);

	public void checkpoint(boolean atEnd) throws Exception;

	public String getName();
}

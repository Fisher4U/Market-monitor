package com.qinyadan.brick.monitor.spi.message.ext;

public interface MessageStatistics {
	public long getBytes();

	public long getOverflowed();

	public long getProduced();

	public void onBytes(int size);

	public void onOverflowed(MessageTree tree);

}

package com.qinyadan.brick.monitor.config;

public interface ServerTransportConfiguration {
	public int getBossThreads();

	public int getWorkerThreads();

	public int getTcpPort();
}

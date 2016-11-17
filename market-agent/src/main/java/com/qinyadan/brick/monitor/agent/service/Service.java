package com.qinyadan.brick.monitor.agent.service;

public interface Service {
	
	public abstract String getName();

	public abstract void start() throws Exception;

	public abstract void stop() throws Exception;

	public abstract boolean isEnabled();

	public abstract boolean isStarted();

	public abstract boolean isStopped();

	public abstract boolean isStartedOrStarting();

	public abstract boolean isStoppedOrStopping();
}

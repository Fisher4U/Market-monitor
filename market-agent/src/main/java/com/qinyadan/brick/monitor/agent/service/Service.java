package com.qinyadan.brick.monitor.agent.service;

public interface Service {

	public String getName();

	public void start() throws Exception;

	public void stop() throws Exception;

	public boolean isEnabled();

	public boolean isStarted();

	public boolean isStopped();

	public boolean isStartedOrStarting();

	public boolean isStoppedOrStopping();
}

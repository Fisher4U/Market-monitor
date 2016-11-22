package com.qinyadan.brick.monitor.agent;

import com.qinyadan.brick.monitor.agent.service.Service;

public interface IAgent extends Service {
	
	public abstract InstrumentationProxy getInstrumentation();

	public abstract void shutdownAsync();

	public abstract void shutdown();
}

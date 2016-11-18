package com.qinyadan.brick.monitor.agent;

import com.qinyadan.brick.monitor.agent.service.Service;

/**
 * 客户端代理时，发送消息到服务端
 *
 */
public interface IAgent extends Service {
	
	public abstract InstrumentationProxy getInstrumentation();

	public abstract void shutdownAsync();

	public abstract void shutdown();
}

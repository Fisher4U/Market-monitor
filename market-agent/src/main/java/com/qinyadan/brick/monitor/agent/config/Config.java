package com.qinyadan.brick.monitor.agent.config;

public interface Config {
	
	public <T> T getProperty(String paramString);

	public <T> T getProperty(String paramString, T paramT);
}

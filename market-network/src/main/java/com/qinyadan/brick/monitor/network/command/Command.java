package com.qinyadan.brick.monitor.network.command;

import java.util.Map;

public interface Command {
	
	public Map<String, String> getArguments();

	public Map<String, String> getHeaders();

	public String getName();

	public long getTimestamp();
}
